package com.ddd.praha.infrastructure;

import com.ddd.praha.application.repository.TaskProgressRepository;
import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.entity.TaskProgress;
import com.ddd.praha.domain.entity.Task;
import com.ddd.praha.domain.model.TaskStatus;
import org.springframework.stereotype.Repository;

/**
 * 参加者課題リポジトリのMyBatis実装
 */
@Repository
public class TaskProgressRepositoryImpl implements TaskProgressRepository {
    
    private final TaskProgressMapper taskProgressMapper;
    
    public TaskProgressRepositoryImpl(TaskProgressMapper taskProgressMapper) {
        this.taskProgressMapper = taskProgressMapper;
    }
    
    @Override
    public TaskProgress findByMemberAndTask(Member member, Task task) {
        TaskProgressRecord record = taskProgressMapper.findByMemberAndTaskRecord(member.getId(), task.getId());
        return record != null ? record.toMemberTask() : null;
    }
    
    @Override
    public void save(TaskProgress taskProgress, Task task) {
        TaskStatus currentStatus = taskProgress.getTaskStatus(task);
        taskProgressMapper.updateTaskStatus(
            taskProgress.getOwner().getId(),
            task.getId(),
            currentStatus.name()
        );
    }
}