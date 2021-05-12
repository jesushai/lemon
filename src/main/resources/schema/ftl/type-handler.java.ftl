package ${packageName};

<#list importPackages as pkg>
import ${pkg};
</#list>
import com.lemon.framework.util.JacksonUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>
 * ${comment!}
 * <p>
 *
 * @author ${author}
 * @since ${date}
 */
public class ${name} extends BaseTypeHandler<${convertType}> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ${convertType} parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public ${convertType} getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return this.toObject(rs.getString(columnName));
    }

    @Override
    public ${convertType} getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return this.toObject(rs.getString(columnIndex));
    }

    @Override
    public ${convertType} getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return this.toObject(cs.getString(columnIndex));
    }

    private String toJson(${convertType} params) {
        return JacksonUtils.toJson(params);
    }

    private ${convertType} toObject(String content) {
        if (null != content && !content.isEmpty()) {
            return JacksonUtils.parseObject(content, ${convertType}.class);
        } else {
            return null;
        }
    }
}
