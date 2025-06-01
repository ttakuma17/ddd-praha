package com.ddd.praha.infrastructure.persistence.typehandler;

import com.ddd.praha.domain.Email;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * EmailのMyBatis TypeHandler
 */
@MappedTypes(Email.class)
public class EmailTypeHandler extends BaseTypeHandler<Email> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Email parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.value());
    }

    @Override
    public Email getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : new Email(value);
    }

    @Override
    public Email getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : new Email(value);
    }

    @Override
    public Email getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : new Email(value);
    }
}