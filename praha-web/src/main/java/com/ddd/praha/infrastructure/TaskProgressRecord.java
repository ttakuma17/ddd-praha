package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.entity.TaskProgress;
import com.ddd.praha.domain.entity.Task;
import com.ddd.praha.domain.model.Email;
import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.MemberId;
import com.ddd.praha.domain.model.MemberName;
import com.ddd.praha.domain.model.TaskId;
import com.ddd.praha.domain.model.TaskName;
import com.ddd.praha.domain.model.TaskStatus;
import java.util.HashMap;
import java.util.Map;

/**
 * 参加者課題のSQLマッピングレコード
 */
public record TaskProgressRecord(
    String memberId,
    String memberName,
    String email,
    String memberStatus,
    String taskId,
    String taskName,
    String taskStatus
) {

  /**
   * ステータス文字列をTaskStatusに変換する
   *
   * @return TaskStatus
   */
  public TaskStatus toTaskStatus() {
    return taskStatus != null ? TaskStatus.valueOf(taskStatus) : TaskStatus.未着手;
  }

  public TaskProgress toMemberTask() {
    Map<Task, TaskStatus> map = new HashMap<>();
    Task task = new Task(
        new TaskId(taskId),
        new TaskName(taskName)
    );
    map.put(task, TaskStatus.valueOf(taskStatus));

    Member member = new Member(
        new MemberId(memberId),
        new MemberName(memberName),
        new Email(email),
        EnrollmentStatus.valueOf(memberStatus)
    );

    return new TaskProgress(
        member,
        map
    );
  }
}