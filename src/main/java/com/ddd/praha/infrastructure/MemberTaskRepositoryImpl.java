package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberTask;
import com.ddd.praha.domain.Task;
import com.ddd.praha.domain.TaskStatus;
import com.ddd.praha.application.repository.MemberTaskRepository;
import com.ddd.praha.application.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    public Optional<MemberTask> findByMember(Member member) {
        MemberTask memberTask = memberTaskMapper.findByMemberId(member.getId().value());
        return Optional.ofNullable(memberTask);
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
}
