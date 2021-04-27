package tools;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.jasypt.intf.service.JasyptStatelessService;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import static com.lemon.app.constants.EntityConstants.*;

/**
 * 名称：MyBatis代码生成器<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/4/17
 */
public class CodeGenerator {

    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    private static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入" + tip + "：");
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.hasLength(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) throws Exception {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        // 输出到子模块
        final String projectPath = System.getProperty("user.dir");

        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("hai-zhang");
        gc.setOpen(false);
        gc.setIdType(IdType.ASSIGN_ID);
        // 注意这块有个坑
        // mysql数据类型datetime会被mp生成器默认生成为LocalDateTime，从而产生异常
        // org.springframework.dao.InvalidDataAccessApiUsageException: Error attempting to get column 'xxx' from result set.
        // Cause: java.sql.SQLFeatureNotSupportedException
        // 解决方案：1.mybatis降到3.5.0版本；2.将生成器默认日期类型改为SQL_PACK
        gc.setDateType(DateType.SQL_PACK);
        // TODO: 记得关掉
//        gc.setFileOverride(true);
        // gc.setSwagger2(true); 实体属性 Swagger2 注解
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(jasyptPBEDecrypt("9EBchHExdurIkG3qj95OcjzlcqTfjl6V40ygUZwL3A4ITKSfWWSFMQExgd/sVivYZg8GuOtBBBBggV7EpYhViRkdYRw3ZuYoas3yrzOBl/cPAQDsUzh1RgS4+DaJnE6PiQq5LMd7dxfGzfkbuuDU8brgXYNeX9UecoljFomtbSeW2jAuGyT4T6Zirj2RlxZT7FbjgQ5S/t+AJHCCGl+h3Z4aBTp1oHurEMeqoVibOIwONP1bdbnMKnH/Rx8in6klQJWCHvC51qxKmm8m2SQfoA==", args));
        // dsc.setSchemaName("public");
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername(jasyptPBEDecrypt("2YPT4O9fdwNk+2kwWrzOs10hIFAb4Ok1f2Bb3oqe4AmnZT3HnOcRHuhB91LBvAKA", args));
        dsc.setPassword(jasyptPBEDecrypt("q400Fvd/996XBHSGoZneYOF89p7nCZChLNBojpwmG97B/Nt9JZlAN2kJ1aw9gtet", args));
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(scanner("模块名"));
        pc.setParent("com.lemon.app");
        mpg.setPackageInfo(pc);

        // 自定义属性注入配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 freemarker
        String templatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        // String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/" + pc.getModuleName()
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        /*
        cfg.setFileCreate(new IFileCreate() {
            @Override
            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
                // 判断自定义文件夹是否需要创建
                checkDir("调用默认方法创建的目录，自定义目录用");
                if (fileType == FileType.MAPPER) {
                    // 已经生成 mapper 文件判断存在，不想重新生成返回 false
                    return !new File(filePath).exists();
                }
                // 允许生成模板文件
                return true;
            }
        });
        */
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();

        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        //数据库表映射到实体的命名策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        //数据库表字段映射到实体类的命名策略
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        //你自己的父类实体,没有就不用设置!
        //自定义继承entity类，添加这一个会在生成实体类的时候继承entity
//        strategy.setSuperEntityClass(com.teysoft.mall.db.entity.BaseEntity.class);
        //实体是否为lombok模型
        strategy.setEntityLombokModel(true);
        //生成@RestController控制器
        strategy.setRestControllerStyle(true);
        //是否继承controller
        //公共父类: 你自己的父类控制器,没有就不用设置!
//        strategy.setSuperControllerClass("");
        //写于父类中的公共字段
//        strategy.setSuperEntityColumns("id_");
        strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
        //驼峰转连字符串
        strategy.setControllerMappingHyphenStyle(true);
        //逻辑删除，如果全局指定了，这里可以不必指定
        strategy.setLogicDeleteFieldName(DB_DELETED_FIELD);
        //乐观锁
        strategy.setVersionFieldName(DB_VERSION_FIELD);
        //表前缀
        strategy.setTablePrefix("");
        //强制指定数据库中的字段名
        strategy.setEntityTableFieldAnnotationEnable(true);
        //自动填充列
        strategy.setTableFillList(new ArrayList<TableFill>(6) {{
            add(new TableFill(DB_CREATE_BY_FIELD, FieldFill.INSERT));
            add(new TableFill(DB_CREATE_NAME_BY_FIELD, FieldFill.INSERT));
            add(new TableFill(DB_CREATE_DATE_FIELD, FieldFill.INSERT));
            add(new TableFill(DB_MODIFIED_BY_FIELD, FieldFill.INSERT_UPDATE));
            add(new TableFill(DB_MODIFIED_NAME_BY_FIELD, FieldFill.INSERT_UPDATE));
            add(new TableFill(DB_MODIFIED_DATE_FIELD, FieldFill.INSERT_UPDATE));
        }});
        //【实体】是否为构建者模型（默认 false）
//        strategy.setEntityBuilderModel(true);
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }

    private static String jasyptPBEDecrypt(String input, String[] args) throws Exception {
        JasyptStatelessService service = new JasyptStatelessService();
        Properties argumentValues = getArgumentValues(args);
        return service.decrypt(
                input,
                argumentValues.getProperty("password"),
                null,
                null,
                argumentValues.getProperty("algorithm"),
                null,
                null,
                argumentValues.getProperty("keyObtentionIterations"),
                null,
                null,
                argumentValues.getProperty("saltGeneratorClassName"),
                null,
                null,
                argumentValues.getProperty("providerName"),
                null,
                null,
                argumentValues.getProperty("providerClassName"),
                null,
                null,
                argumentValues.getProperty("stringOutputType"),
                null,
                null,
                argumentValues.getProperty("ivGeneratorClassName"),
                null,
                null);
    }

    private static Properties getArgumentValues(String[] args) throws Exception {
        Properties argumentValues = new Properties();
        for (String arg : args) {
            int pos = arg.indexOf('=');
            if (pos > 0) {
                String key = arg.substring(0, pos);
                String value = arg.substring(pos + 1);
                if (!StringUtils.hasLength(key) || !StringUtils.hasLength(value)) {
                    throw new Exception("Bad argument: " + key);
                }
                argumentValues.setProperty(key, value);
            }
        }
        if (null == argumentValues.getProperty("password")) {
            throw new Exception("Must be entered \"password\".");
        }

        // 固定项，所以实际就提供一个password即可
        argumentValues.setProperty("ivGeneratorClassName", "org.jasypt.iv.RandomIvGenerator");
        argumentValues.setProperty("algorithm", "PBEWITHHMACSHA512ANDAES_256");

        return argumentValues;
    }
}
