package com.lemon.schemaql.util;

import com.lemon.schemaql.engine.helper.ProjectSchemaHelper;

import java.util.Optional;
import java.util.Properties;

/**
 * 名称：Schema代码生成器<p>
 * 描述：<p>
 * 根据schema的json描述自动生成代码
 *
 * @author hai-zhang
 * @since 2021/4/27
 */
public class SchemaCodeGenerator {

    public static void main(String[] args) {
        final Properties properties = getArguments(args);

        // 参数：输出到子模块
        String projectPath = Optional
                .ofNullable(properties.getProperty("project-path"))
                .orElse(System.getProperty("user.dir"));
        // 参数：schema json路径
        String jsonPath = Optional
                .ofNullable(properties.getProperty("schema-json-path"))
                .orElse("src/main/resources/schema/json");
        // 参数：指定模块
        String moduleName = Optional
                .ofNullable(properties.getProperty("module-name"))
                .orElse("");
        // 参数：要生成的schema项目
        String itemName = Optional
                .ofNullable(properties.getProperty("items"))
                .orElse("");

        ProjectSchemaHelper.load(projectPath + '/' + jsonPath);
    }

    private static Properties getArguments(String[] args) {
        // 分解每一组参数
        Properties properties = new Properties();
        for (String arg : args) {
            int inx = arg.indexOf('=');
            if (inx > 0) {
                String key = arg.substring(0, inx);
                String value = arg.substring(inx + 1);
                properties.setProperty(key, value);
            }
        }
        return properties;
    }
}
