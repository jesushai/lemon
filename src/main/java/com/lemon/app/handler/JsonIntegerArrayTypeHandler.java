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
 * Json转Integer[]
 * <p>
 *
 * @author hai-zhang
 * @since 2021-05-11
 */
public class JsonIntegerArrayTypeHandler extends BaseTypeHandler<Integer[]> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Integer[] parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public Integer[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return this.toObject(rs.getString(columnName));
    }

    @Override
    public Integer[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return this.toObject(rs.getString(columnIndex));
    }

    @Override
    public Integer[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return this.toObject(cs.getString(columnIndex));
    }

    private String toJson(Integer[] params) {
        return JacksonUtils.toJson(params);
    }

    private Integer[] toObject(String content) {
        if (null != content && !content.isEmpty()) {
            return JacksonUtils.parseObject(content, Integer[].class);
        } else {
            return null;
        }
    }
}
