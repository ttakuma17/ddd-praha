package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.TaskStatus;

/**
 * 参加者課題のSQLマッピングレコード
 */
public record MemberTaskRecord(
    String memberId,
    String taskId,
    String status
) {

    /**
     * ステータス文字列をTaskStatusに変換する
     * @return TaskStatus
     */
    public TaskStatus toTaskStatus() {
        return status != null ? TaskStatus.valueOf(status) : TaskStatus.未着手;
    }
}