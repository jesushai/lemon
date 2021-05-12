package com.lemon.app.handler;

import com.lemon.framework.util.JacksonUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>
 * Json转String[]
 * <p>
 *
 * @author hai-zhang
 * @since 2021-05-11
 */
public class JsonStringArrayTypeHandler extends BaseTypeHandler<String[]> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String[] parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public String[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return this.toObject(rs.getString(columnName));
    }

    @Override
    public String[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return this.toObject(rs.getString(columnIndex));
    }

    @Override
    public String[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return this.toObject(cs.getString(columnIndex));
    }

    private String toJson(String[] params) {
        return JacksonUtils.toJson(params);
    }

    private String[] toObject(String content) {
        if (null != content && !content.isEmpty()) {
            return JacksonUtils.parseObject(content, String[].class);
        } else {
            return null;
        }
    }
}
