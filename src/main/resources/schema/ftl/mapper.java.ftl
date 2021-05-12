package ${packageName};

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
<#if entityClassPackage != mapperClassPackage>
import ${entityClassPackage};
</#if>

/**
 * <p>
 * ${entityComment!} Mapper
 * <p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if kotlin>
interface ${mapperName} : BaseMapper<${entityName}>
<#else>
public interface ${mapperName} extends BaseMapper<${entityName}> {

}
</#if>
