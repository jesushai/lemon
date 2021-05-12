<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${mapperClassPackage}">

<#if enableCache>
    <!-- 开启二级缓存 -->
    <cache type="org.mybatis.caches.ehcache.LoggingEhcache"/>

</#if>
<#if baseResultMap>
    <!-- 通用查询映射结果 -->
    <resultMap id="BaseResultMap" type="${entityClassPackage}">
<#list fields as field>
<#if field.idFlag><#--生成主键排在第一位-->
        <id column="${field.columnName}" property="${field.name}" />
</#if>
</#list>
<#list fields as field><#--生成普通字段 -->
<#if !field.idFlag && !field.transientFlag>
    <#if field.typeHandler?? && field.typeHandlerConfig??>
        <result column="${field.columnName}" property="${field.name}" typeHandler="${field.typeHandlerConfig.packageName}.${field.typeHandler}" />
    <#else>
        <result column="${field.columnName}" property="${field.name}" />
    </#if>
</#if>
</#list>
    </resultMap>

</#if>
<#if baseColumnList>
    <!-- 通用查询结果列 -->
    <sql id="BaseColumns">
<#list fields as field>
<#if field_index == fields?size - 1>
        ${field.columnName}
<#else>
        ${field.columnName},
</#if>
</#list>
    </sql>

</#if>
</mapper>
