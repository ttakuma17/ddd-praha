package com.ddd.praha.infrastructure.persistence.typehandler;

import com.ddd.praha.domain.TeamName;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TeamNameのMyBatis TypeHandler
 */
@MappedTypes(TeamName.class)
public class TeamNameTypeHandler extends BaseTypeHandler<TeamName> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, TeamName parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.value());
    }

    @Override
    public TeamName getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : new TeamName(value);
    }

    @Override
    public TeamName getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : new TeamName(value);
    }

    @Override
    public TeamName getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : new TeamName(value);
    }
}