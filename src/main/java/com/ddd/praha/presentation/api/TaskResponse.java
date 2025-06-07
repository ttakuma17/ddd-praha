package com.ddd.praha.presentation.api;

import com.ddd.praha.domain.Task;

/**
 * タスクレスポンス
 */
public record TaskResponse(
        String id,
        String name
) {
    public static TaskResponse fromDomain(Task task) {
        return new TaskResponse(
                task.getId().value(),
                task.getName().value()
        );
    }
}