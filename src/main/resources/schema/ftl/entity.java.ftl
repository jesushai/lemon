package ${packageName};

import com.baomidou.mybatisplus.annotation.*;
<#list importPackages as pkg>
import ${pkg};
</#list>
<#if entityLombokModel>
import lombok.Data;
import lombok.EqualsAndHashCode;
    <#if chainModel>
import lombok.experimental.Accessors;
    </#if>
</#if>

/**
 * <p>
 * ${entity.comment!}
 * <p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if entityLombokModel>
@Data
    <#if superEntityClass??>
@EqualsAndHashCode(callSuper = true)
    <#else>
@EqualsAndHashCode(callSuper = false)
    </#if>
    <#if chainModel>
@Accessors(chain = true)
    </#if>
</#if>
@TableName("${entity.tableName}")
<#if superEntityClass??>
public class ${entity.entityName} extends ${superEntityClass}<#if activeRecord><${entity.entityName}></#if> {
<#elseif activeRecord>
public class ${entity.entityName} extends Model<${entity.entityName}> {
<#else>
public class ${entity.entityName} implements Serializable {
</#if>

<#if entitySerialVersionUID>
    private static final long serialVersionUID = 1L;
</#if>
<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list entity.fields as field>
    <#if field.idFlag>
        <#assign keyPropertyName="${field.name}"/>
    </#if>

    <#if field.comment!?length gt 0>
    /**
     * ${field.comment}
     */
    </#if>
    <#if field.idFlag>
    <#-- 主键 -->
        <#if field.idType??>
            <#if field.name == field.columnName>
    @TableId(type = IdType.${field.idType})
            <#else>
    @TableId(value = "${field.columnName}", type = IdType.${field.idType})
            </#if>
        <#else>
            <#if field.name == field.columnName>
    @TableId
            <#else>
    @TableId("${field.columnName}")
            </#if>
        </#if>
    <#elseif field.fill??>
    <#-- -----   存在字段填充设置   ----->
        <#if field.name == field.columnName>
    @TableField(fill = FieldFill.${field.fill})
        <#else>
    @TableField(value = "${field.columnName}", fill = FieldFill.${field.fill})
        </#if>
    <#elseif field.transientFlag?? || field.transientFlag>
    <#-- 非持久字段 -->
    @TableField(exist = false)
    <#elseif field.typeHandler??>
    <#-- 类型转换 -->
        <#if field.name == field.columnName>
    @TableField(typeHandler = ${field.typeHandler}.class)
        <#else>
    @TableField(value = "${field.columnName}", typeHandler = ${field.typeHandler}.class)
        </#if>
    <#else>
    <#-- 普通字段 -->
        <#if field.name == field.columnName>
    @TableField
        <#else>
    @TableField("${field.columnName}")
        </#if>
    </#if>
    <#-- 乐观锁注解 -->
    <#if (versionColumn!"") == field.columnName>
    @Version
    </#if>
    <#-- 逻辑删除注解 -->
    <#if (deletedColumn!"") == field.columnName>
    @TableLogic
    </#if>
    <#if !field.idFlag && field.allowNull!true && field.defaultValue??>
        <#if field.type == "String">
    private ${field.type} ${field.name} = "${field.defaultValue}";
        <#elseif field.type == "Long" || field.type == "long">
    private ${field.type} ${field.name} = ${field.defaultValue}L;
        <#elseif field.type == "Float" || field.type == "float">
    private ${field.type} ${field.name} = ${field.defaultValue}f;
        <#elseif field.type == "BigDecimal">
    private ${field.type} ${field.name} = BigDecimal.valueOf(${field.defaultValue});
        <#else>
    private ${field.type} ${field.name} = ${field.defaultValue};
        </#if>
    <#else>
    private ${field.type} ${field.name};
    </#if>
</#list>
<#------------  END 字段循环遍历  ---------->

<#if !entityLombokModel>
    <#list entity.fields as field>
        <#if field.type == "boolean">
            <#assign getprefix="is"/>
        <#else>
            <#assign getprefix="get"/>
        </#if>
    public ${field.type} ${getprefix}${field.name?cap_first}() {
        return ${field.name};
    }

    <#if chainModel>
    public ${entity} set${field.name?cap_first}(${field.type} ${field.name}) {
    <#else>
    public void set${field.name?cap_first}(${field.type} ${field.name}) {
    </#if>
        this.${field.name} = ${field.name};
        <#if chainModel>
        return this;
        </#if>
    }
    </#list>
</#if>

<#if activeRecord>
    @Override
    protected Serializable pkVal() {
    <#if keyPropertyName??>
        return this.${keyPropertyName};
    <#else>
        return null;
    </#if>
    }

</#if>
<#if !entityLombokModel>
    @Override
    public String toString() {
        return "${entity}{" +
    <#list entity.fields as field>
        <#if field_index==0>
            "${field.name}=" + ${field.name} +
        <#else>
            ", ${field.name}=" + ${field.name} +
        </#if>
    </#list>
        "}";
    }
</#if>
}
