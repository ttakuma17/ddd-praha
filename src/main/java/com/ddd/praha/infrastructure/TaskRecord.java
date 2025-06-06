package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.Task;
import com.ddd.praha.domain.TaskId;
import com.ddd.praha.domain.TaskName;

/**
 * 課題のSQLマッピングレコード
 */
public record TaskRecord(
    String id,
    String name
) {

    /**
     * ドメインのTaskオブジェクトに変換する
     * @return Task
     */
    public Task toTask() {
        return new Task(
            new TaskId(id),
            new TaskName(name)
        );
    }
}
