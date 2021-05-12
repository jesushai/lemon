package com.lemon.schemaql.util.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.SystemException;
import com.lemon.framework.util.LoggerUtils;
import com.lemon.framework.util.TimestampUtils;
import com.lemon.framework.util.io.FileUtil;
import com.lemon.schemaql.config.EntitySchemaConfig;
import com.lemon.schemaql.config.EnumSchemaConfig;
import com.lemon.schemaql.config.FieldSchemaConfig;
import com.lemon.schemaql.config.ModuleSchemaConfig;
import com.lemon.schemaql.config.support.TypeHandlerConfig;
import com.lemon.schemaql.engine.helper.ModuleSchemaHelper;
import com.lemon.schemaql.meta.FieldMeta;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static com.lemon.schemaql.engine.SchemaQlContext.globalConfig;
import static com.lemon.schemaql.engine.SchemaQlContext.projectSchemaConfig;

/**
 * 名称：模块代码生成器<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/27
 */
@Slf4j
public class ModuleCodeGenerator {

    /**
     * 根据schema路径下的json文件生成源码
     *
     * @param schemaPath schema路径，默认是{@code src/main/resources/schema}
     *                   <p>
     *                   路径下应该包含json子路径和ftl子路径
     *                   <p>
     *                   json子路径保存了项目配置的相关信息
     *                   <p>
     *                   ftl子路径保存了freemarker的模板文件，可以根据要求自行调整
     */
    @SneakyThrows
    public void generate(@NonNull String schemaPath) {
        Configuration config = new Configuration(Configuration.VERSION_2_3_31);
        config.setDirectoryForTemplateLoading(new File(FileUtil.mergePath(schemaPath, "ftl")));

        if (CollectionUtils.isNotEmpty(projectSchemaConfig().getModuleSchemas())) {
            for (ModuleSchemaConfig moduleSchemaConfig : projectSchemaConfig().getModuleSchemas()) {
                generate(moduleSchemaConfig, config);
            }
        }
    }

    private void generate(@NonNull ModuleSchemaConfig moduleSchemaConfig, @NonNull Configuration config) throws IOException {
        LoggerUtils.debug(log, "--------------------------------------------------");
        LoggerUtils.debug(log, "--- start generating module-[{}] codes... ", moduleSchemaConfig.getModuleName());
        String sourcePath = mkSourceDir(moduleSchemaConfig);
        String resourcePath = mkResourceDir(moduleSchemaConfig);
        LoggerUtils.debug(log, "|-- source code path: {}", sourcePath);
        LoggerUtils.debug(log, "|-- resource path: {}", resourcePath);
        // 循环sub module，创建子模块目录
        if (CollectionUtils.isNotEmpty(moduleSchemaConfig.getSubModuleNames())) {
            for (String sub : moduleSchemaConfig.getSubModuleNames()) {
                String path = FileUtil.mergePath(sourcePath, sub);
                LoggerUtils.debug(log, "|-- create sub module path: {}", path);
                FileUtils.forceMkdir(new File(path));
            }
        }
        // 创建枚举
        LoggerUtils.debug(log, "|-- generating type enums...");
        generateEnum(moduleSchemaConfig, config);
        LoggerUtils.debug(log, "|-- enums generation complete.");
        // 创建Handler
        LoggerUtils.debug(log, "|-- generating type handlers...");
        generateTypeHandler(moduleSchemaConfig, config);
        LoggerUtils.debug(log, "|-- type handlers generation complete.");
        // 创建实体
        LoggerUtils.debug(log, "|-- generating entities...");
        generateEntity(moduleSchemaConfig, config);
        LoggerUtils.debug(log, "|-- entities generation complete.");
    }

    private void generateEnum(ModuleSchemaConfig moduleSchema, Configuration config) throws IOException {
        if (CollectionUtils.isEmpty(moduleSchema.getEntitySchemas())) {
            return;
        }
        Template template = config.getTemplate("enum.java.ftl");

        final Map<String, Object> data = new HashMap<>();
        data.put("packageName", moduleSchema.getBasePackage() + '.' + moduleSchema.getEnumBasePackage());
        data.put("author", Optional.ofNullable(globalConfig().getAuthor())
                .orElse(org.apache.commons.lang3.StringUtils.EMPTY));
        data.put("date", TimestampUtils.parseToDate(TimestampUtils.now(), '-'));

        for (EnumSchemaConfig enumSchema : moduleSchema.getEnumSchemas()) {
            data.put("enum", enumSchema);

            writeSourceFile(
                    data,
                    FileUtil.mergePath(
                            projectSchemaConfig().getProjectPath(),
                            moduleSchema.getModulePath(),
                            moduleSchema.getSourcesPath(),
                            enumSchema.getEnumClassName().replace('.', File.separatorChar)+ ".java"
                    ),
                    template);
        }
    }

    private void generateEntity(ModuleSchemaConfig moduleSchema, Configuration config) throws IOException {
        if (CollectionUtils.isEmpty(moduleSchema.getEntitySchemas())) {
            return;
        }
        Template template = config.getTemplate("entity.java.ftl");
        Template templateMapper = config.getTemplate("mapper.java.ftl");
        Template templateMapperXml = config.getTemplate("mapper.xml.ftl");

        final Map<String, Object> data = new HashMap<>();
        final Map<String, Object> mapperData = new HashMap<>();

        // 全局属性
        data.put("entityLombokModel", true);
        data.put("chainModel", true);
        data.put("activeRecord", false);
        data.put("entitySerialVersionUID", true);
        data.put("versionColumn", projectSchemaConfig().getEntitySettings().getVersionColumn());
        data.put("deletedColumn", projectSchemaConfig().getEntitySettings().getDeletedColumn());
        data.put("author", Optional.ofNullable(globalConfig().getAuthor())
                .orElse(org.apache.commons.lang3.StringUtils.EMPTY));
        data.put("date", TimestampUtils.parseToDate(TimestampUtils.now(), '-'));

        mapperData.put("author", data.get("author"));
        mapperData.put("date", data.get("date"));
        mapperData.put("kotlin", false);
        mapperData.put("enableCache", false);
        mapperData.put("baseResultMap", true);
        mapperData.put("baseColumnList", true);

        for (EntitySchemaConfig entitySchema : moduleSchema.getEntitySchemas()) {
            String entityFullClass = entitySchema.getEntityClassName();
            String mapperFullClass = entitySchema.getMapperClassName();

            data.put("packageName", entityFullClass.substring(0, entityFullClass.lastIndexOf('.')));
            data.put("entity", entitySchema);
            generateEntity(moduleSchema, entitySchema, data, template);

            mapperData.put("packageName", mapperFullClass.substring(0, mapperFullClass.lastIndexOf('.')));
            mapperData.put("entityClassPackage", entityFullClass);
            mapperData.put("mapperClassPackage", mapperFullClass);
            mapperData.put("fields", entitySchema.getFields());
            mapperData.put("entityComment", entitySchema.getComment());
            mapperData.put("mapperName", entitySchema.getMapperName());
            mapperData.put("entityName", entitySchema.getEntityName());
            generateMapper(moduleSchema, entitySchema, mapperData, templateMapper);
            generateMapperXml(moduleSchema, entitySchema, mapperData, templateMapperXml);
        }
    }

    private void generateMapperXml(ModuleSchemaConfig moduleSchema, EntitySchemaConfig entitySchema, Map<String, Object> data, Template template) {
        String path = FileUtil.mergePath(
                projectSchemaConfig().getProjectPath(),
                moduleSchema.getModulePath(),
                moduleSchema.getResourcePath(),
                "mapper"
        );

        if (StringUtils.hasText(entitySchema.getSubModuleName())) {
            path = path + File.separatorChar + entitySchema.getSubModuleName();
        }

        writeSourceFile(
                data,
                path + File.separatorChar + entitySchema.getMapperName() + ".xml",
                template);
    }

    private void generateMapper(ModuleSchemaConfig moduleSchema, EntitySchemaConfig entitySchema, Map<String, Object> data, Template template) {
        writeSourceFile(
                data,
                FileUtil.mergePath(
                        projectSchemaConfig().getProjectPath(),
                        moduleSchema.getModulePath(),
                        moduleSchema.getSourcesPath(),
                        entitySchema.getMapperClassName().replace('.', File.separatorChar) + ".java"
                ),
                template);
    }

    private void generateEntity(ModuleSchemaConfig moduleSchema, EntitySchemaConfig entitySchema, Map<String, Object> data, Template template) {
        boolean hasSubModule = StringUtils.hasText(entitySchema.getSubModuleName());
        if (hasSubModule && (null == moduleSchema.getSubModuleNames() || !moduleSchema.getSubModuleNames().contains(entitySchema.getSubModuleName()))) {
            // 实体所在子模块丢失！
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate("Entity {0} is under sub module {1}, but there is not contains in the main module {2}.")
                    .args(entitySchema.getEntityName(), entitySchema.getSubModuleName(), moduleSchema.getModuleName())
                    .throwIt();
        }

        Set<String> importPackages = new HashSet<>();
        data.put("importPackages", importPackages);

        // 主键策略
        FieldSchemaConfig idSchema = entitySchema.getFields().stream()
                .filter(FieldMeta::getIdFlag)
                .findFirst()
                .orElse(null);
        if (null == idSchema) {
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate("Entity {0} without a primary key.")
                    .args(entitySchema.getEntityName())
                    .throwIt();
        }

        if (null == idSchema.getIdType()) {
            // 使用全局配置
            idSchema.setIdType(projectSchemaConfig().getEntitySettings().getIdType());
        }

        // imports
        String enumPackage = null;
        if (StringUtils.hasText(moduleSchema.getEnumBasePackage())) {
            enumPackage = moduleSchema.getBasePackage() + '.' + moduleSchema.getEnumBasePackage() + ".*";
        }

        if ((Boolean) data.get("activeRecord")) {
            importPackages.add("com.baomidou.mybatisplus.extension.activerecord.Model");
        } else {
            importPackages.add("java.io.Serializable");
        }
        for (FieldSchemaConfig field : entitySchema.getFields()) {
            // 是否填充项
            if (Objects.equals(projectSchemaConfig().getEntitySettings().getCreateByColumn(), field.getColumnName()) ||
                    Objects.equals(projectSchemaConfig().getEntitySettings().getCreateNameByColumn(), field.getColumnName()) ||
                    Objects.equals(projectSchemaConfig().getEntitySettings().getCreateTimeColumn(), field.getColumnName())) {
                field.setFill(FieldFill.INSERT);
            } else if (Objects.equals(projectSchemaConfig().getEntitySettings().getModifiedByColumn(), field.getColumnName()) ||
                    Objects.equals(projectSchemaConfig().getEntitySettings().getModifiedNameByColumn(), field.getColumnName()) ||
                    Objects.equals(projectSchemaConfig().getEntitySettings().getModifiedTimeColumn(), field.getColumnName())) {
                field.setFill(FieldFill.INSERT_UPDATE);
            }
            // 是否类型转换
            // 如果是VO值对象，则必须提供类型转换器
            if (StringUtils.hasText(field.getTypeHandler())) {
                TypeHandlerConfig typeHandler = moduleSchema.getTypeHandlerConfigs().stream()
                        .filter(x -> x.getName().equals(field.getTypeHandler()))
                        .findFirst()
                        .orElseThrow(() -> new ExceptionBuilder<>(SystemException.class)
                                .messageTemplate("The type handler of entity field \"{0}.{1}\" is not defined in module \"{2}\".")
                                .args(entitySchema.getEntityName(), field.getName(), moduleSchema.getModuleName())
                                .build());
                if (!data.get("packageName").equals(typeHandler.getPackageName())) {
                    importPackages.add(typeHandler.getPackageName());
                }
            }
            // 普通类型中需要引包的
            if ("BigDecimal".equals(field.getType())) {
                importPackages.add("java.math.BigDecimal");
            } else if ("Timestamp".equals(field.getType())) {
                importPackages.add("java.sql.Timestamp");
            } else if ("Date".equals(field.getType())) {
                importPackages.add("java.sql.Date");
            }
            // 枚举
            if (field.getType().endsWith("Enum") && null != enumPackage) {
                importPackages.add(enumPackage);
            }
        }

        writeSourceFile(
                data,
                FileUtil.mergePath(
                        projectSchemaConfig().getProjectPath(),
                        moduleSchema.getModulePath(),
                        moduleSchema.getSourcesPath(),
                        entitySchema.getEntityClassName().replace('.', File.separatorChar) + ".java"
                ),
                template);
    }

    private void generateTypeHandler(ModuleSchemaConfig moduleSchemaConfig, Configuration config) throws IOException {
        Set<TypeHandlerConfig> typeHandlerConfigs = moduleSchemaConfig.getTypeHandlerConfigs();
        if (null == typeHandlerConfigs || typeHandlerConfigs.size() <= 0)
            return;

        Template template = config.getTemplate("type-handler.java.ftl");

        // 循环所有的type-handler
        Map<String, Object> data = new HashMap<>();
        data.put("author", Optional.ofNullable(globalConfig().getAuthor())
                .orElse(org.apache.commons.lang3.StringUtils.EMPTY));
        data.put("date", TimestampUtils.parseToDate(TimestampUtils.now(), '-'));

        for (TypeHandlerConfig typeHandlerConfig : typeHandlerConfigs) {
            // 创建模板填充数据
            data.put("packageName", typeHandlerConfig.getPackageName());

            List<String> imports = new ArrayList<>();
            if (StringUtils.hasText(typeHandlerConfig.getConvertType())) {
                int pos = typeHandlerConfig.getConvertType().lastIndexOf('.');
                if (pos > 0) {
                    imports.add(typeHandlerConfig.getConvertType().substring(0, pos));
                    typeHandlerConfig.setConvertType(typeHandlerConfig.getConvertType().substring(pos + 1));
                }
            }
            data.put("importPackages", imports);

            data.put("comment", typeHandlerConfig.getComment());
            data.put("name", typeHandlerConfig.getName());
            data.put("convertType", typeHandlerConfig.getConvertType());

            writeSourceFile(
                    data,
                    FileUtil.mergePath(
                            projectSchemaConfig().getProjectPath(),
                            moduleSchemaConfig.getModulePath(),
                            moduleSchemaConfig.getSourcesPath(),
                            typeHandlerConfig.getPackageName().replace('.', File.separatorChar),
                            typeHandlerConfig.getName() + ".java"
                    ),
                    template);
        }
    }

    /**
     * 根据ftl模板写入文件
     * <p>
     * 如果源码文件已存在则忽略
     *
     * @param data           填充数据
     * @param sourceFilepath 源码文件路径
     * @param template       flt模板
     */
    private void writeSourceFile(Map<String, Object> data, String sourceFilepath, Template template) {

        File file = new File(sourceFilepath);
        if (file.exists()) {
            return;
        }

        try {
            FileUtils.forceMkdirParent(file);
            LoggerUtils.debug(log, "  |-- generate \"{}\"", sourceFilepath);
            file.createNewFile();
        } catch (IOException e) {
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate("Create new file \"{0}\" error: {1}")
                    .args(sourceFilepath, e.getMessage())
                    .throwIt();
        }

        try (Writer out = new FileWriter(file)) {
            template.process(data, out);
            out.flush();
        } catch (IOException e) {
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate("File \"{0}\" not found.")
                    .args(sourceFilepath)
                    .throwIt();
        } catch (TemplateException e) {
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate(e.getMessage())
                    .throwIt();
        }
    }

    /**
     * 创建模块源码所在路径，包含了模块源码的包路径，如果路径已存在则什么都不做
     *
     * @param moduleSchemaConfig 模块
     * @return 路径
     * @throws IOException 创建路径失败
     */
    private String mkSourceDir(@NonNull ModuleSchemaConfig moduleSchemaConfig) throws IOException {
        String path = FileUtil.mergePath(
                projectSchemaConfig().getProjectPath(),
                moduleSchemaConfig.getModulePath(),
                moduleSchemaConfig.getSourcesPath(),
                moduleSchemaConfig.getBasePackage().replace('.', File.separatorChar)
        );
        FileUtils.forceMkdir(new File(path));
        return path;
    }

    /**
     * 创建模块资源路径，如果路径已存在则什么都不做
     *
     * @param moduleSchemaConfig 模块
     * @return 路径
     * @throws IOException 创建路径失败
     */
    private String mkResourceDir(@NonNull ModuleSchemaConfig moduleSchemaConfig) throws IOException {
        String path = FileUtil.mergePath(
                projectSchemaConfig().getProjectPath(),
                moduleSchemaConfig.getModulePath(),
                moduleSchemaConfig.getResourcePath()
        );
        FileUtils.forceMkdir(new File(path));
        return path;
    }
}
