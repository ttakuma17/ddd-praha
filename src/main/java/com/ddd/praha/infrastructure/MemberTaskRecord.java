package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.Email;
import com.ddd.praha.domain.EnrollmentStatus;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.MemberName;
import com.ddd.praha.domain.MemberTask;
import com.ddd.praha.domain.Task;
import com.ddd.praha.domain.TaskId;
import com.ddd.praha.domain.TaskName;
import com.ddd.praha.domain.TaskStatus;
import java.util.HashMap;
import java.util.Map;

/**
 * 参加者課題のSQLマッピングレコード
 */
public record MemberTaskRecord(
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

  public MemberTask toMemberTask() {
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

    return new MemberTask(
        member,
        map
    );
  }
}