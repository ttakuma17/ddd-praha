package com.ddd.praha.infrastructure.persistence.typehandler;

import com.ddd.praha.domain.EnrollmentStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * EnrollmentStatusのMyBatis TypeHandler
 */
@MappedTypes(EnrollmentStatus.class)
public class EnrollmentStatusTypeHandler extends BaseTypeHandler<EnrollmentStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EnrollmentStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public EnrollmentStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : EnrollmentStatus.valueOf(value);
    }

    @Override
    public EnrollmentStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : EnrollmentStatus.valueOf(value);
    }

    @Override
    public EnrollmentStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : EnrollmentStatus.valueOf(value);
    }
}