package com.ddd.praha.infrastructure;


import com.ddd.praha.application.repository.MemberTaskRepository;
import com.ddd.praha.application.repository.TaskRepository;
import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.entity.MemberTask;
import com.ddd.praha.domain.entity.Task;
import com.ddd.praha.domain.model.TaskId;
import com.ddd.praha.domain.model.TaskStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 参加者課題リポジトリのMyBatis実装
 */
@Repository
public class MemberTaskRepositoryImpl implements MemberTaskRepository {
    private final MemberTaskMapper memberTaskMapper;
    private final TaskRepository taskRepository;

    public MemberTaskRepositoryImpl(MemberTaskMapper memberTaskMapper, @Qualifier("taskRepositoryImpl") TaskRepository taskRepository) {
        this.memberTaskMapper = memberTaskMapper;
        this.taskRepository = taskRepository;
    }

    @Override
    public MemberTask getByMember(Member member) {
        MemberTaskRecord memberTaskRecord = memberTaskMapper.findByMemberId(member.getId());
        if (memberTaskRecord == null) {
            throw new IllegalStateException("Member task record is null.");
        }
        return memberTaskRecord.toMemberTask();
    }

    @Override
    public List<MemberTask> findByTask(Task task) {
        return memberTaskMapper.findByTaskId(task.getId().value());
    }

    @Override
    public MemberTask save(MemberTask memberTask) {
        // 既存のエントリを削除（同じ所有者のものがあれば）
        memberTaskMapper.deleteAllByMemberId(memberTask.getOwner().getId().value());

        // 全てのタスクを取得
        List<Task> allTasks = taskRepository.findAll();

        // 各タスクのステータスを保存
        for (Task task : allTasks) {
            TaskStatus status = memberTask.getTaskStatus(task);
            if (status != null) {
                memberTaskMapper.insert(
                    memberTask.getOwner().getId().value(),
                    task.getId().value(),
                    status.name()
                );
            }
        }

        return memberTask;
    }

    @Override
    public List<Member> findMembersByTasksAndStatuses(List<TaskId> taskIds, List<TaskStatus> statuses, int page, int size) {
        List<String> taskIdStrings = taskIds.stream()
            .map(TaskId::value)
            .collect(Collectors.toList());
        List<String> statusStrings = statuses.stream()
            .map(TaskStatus::name)
            .collect(Collectors.toList());
        
        return memberTaskMapper.findMembersByTasksAndStatuses(taskIdStrings, statusStrings, page * size, size);
    }

    @Override
    public long countMembersByTasksAndStatuses(List<TaskId> taskIds, List<TaskStatus> statuses) {
        List<String> taskIdStrings = taskIds.stream()
            .map(TaskId::value)
            .collect(Collectors.toList());
        List<String> statusStrings = statuses.stream()
            .map(TaskStatus::name)
            .collect(Collectors.toList());
        
        return memberTaskMapper.countMembersByTasksAndStatuses(taskIdStrings, statusStrings);
    }
}
