package com.ddd.praha.infrastructure.persistence.typehandler;

import com.ddd.praha.domain.MemberName;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MemberNameのMyBatis TypeHandler
 */
@MappedTypes(MemberName.class)
public class MemberNameTypeHandler extends BaseTypeHandler<MemberName> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, MemberName parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.value());
    }

    @Override
    public MemberName getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : new MemberName(value);
    }

    @Override
    public MemberName getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : new MemberName(value);
    }

    @Override
    public MemberName getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : new MemberName(value);
    }
}