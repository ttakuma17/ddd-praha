package com.ddd.praha.infrastructure.persistence.typehandler;

import com.ddd.praha.domain.TeamId;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TeamIdのMyBatis TypeHandler
 */
@MappedTypes(TeamId.class)
public class TeamIdTypeHandler extends BaseTypeHandler<TeamId> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, TeamId parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.value());
    }

    @Override
    public TeamId getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : new TeamId(value);
    }

    @Override
    public TeamId getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : new TeamId(value);
    }

    @Override
    public TeamId getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : new TeamId(value);
    }
}