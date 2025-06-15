package com.ddd.praha.infrastructure;

import com.ddd.praha.application.repository.MemberTaskRepository;
import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.entity.MemberTask;
import com.ddd.praha.domain.entity.Task;
import com.ddd.praha.domain.model.TaskStatus;
import org.springframework.stereotype.Repository;

/**
 * 参加者課題リポジトリのMyBatis実装
 */
@Repository
public class MemberTaskRepositoryImpl implements MemberTaskRepository {
    
    private final MemberTaskMapper memberTaskMapper;
    
    public MemberTaskRepositoryImpl(MemberTaskMapper memberTaskMapper) {
        this.memberTaskMapper = memberTaskMapper;
    }
    
    @Override
    public MemberTask findByMemberAndTask(Member member, Task task) {
        MemberTaskRecord record = memberTaskMapper.findByMemberAndTaskRecord(member.getId(), task.getId());
        return record != null ? record.toMemberTask() : null;
    }
    
    @Override
    public void save(MemberTask memberTask, Task task) {
        TaskStatus currentStatus = memberTask.getTaskStatus(task);
        memberTaskMapper.updateTaskStatus(
            memberTask.getOwner().getId(), 
            task.getId(),
            currentStatus.name()
        );
    }
}