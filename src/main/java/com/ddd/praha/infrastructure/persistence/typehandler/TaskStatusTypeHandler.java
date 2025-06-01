package com.ddd.praha.infrastructure.persistence.typehandler;

import com.ddd.praha.domain.TaskStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TaskStatusのMyBatis TypeHandler
 */
@MappedTypes(TaskStatus.class)
public class TaskStatusTypeHandler extends BaseTypeHandler<TaskStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, TaskStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public TaskStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : TaskStatus.valueOf(value);
    }

    @Override
    public TaskStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : TaskStatus.valueOf(value);
    }

    @Override
    public TaskStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : TaskStatus.valueOf(value);
    }
}