package com.ddd.praha.infrastructure;

import com.ddd.praha.application.repository.MemberRepository;
import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.MemberId;
import com.ddd.praha.domain.model.TaskId;
import com.ddd.praha.domain.model.TaskStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * メンバーリポジトリのMyBatis実装
 */
@Repository
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberMapper memberMapper;

    public MemberRepositoryImpl(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Override
    public void save(Member member) {
        // メールアドレスの重複チェック
        MemberRecord existingMember = memberMapper.findByEmail(member.getEmail().value());
        if (existingMember != null && !existingMember.id().equals(member.getId().value())) {
            throw new IllegalArgumentException("このメールアドレスは既に使用されています");
        }
        
        memberMapper.insert(member);
    }

    @Override
    public Member get(MemberId id) {
        MemberRecord memberRecord = memberMapper.get(id);
        if (memberRecord == null) {
            throw new IllegalStateException("Member record is null.");
        }
        return memberRecord.toMember();
    }

    @Override
    public List<Member> getAll() {
        List<MemberRecord> membersRecord = memberMapper.getAll();
        if (membersRecord == null) {
            throw new IllegalStateException("Member records are null. Please check the database connection and table definition.");
        }
        return membersRecord.stream().map(MemberRecord::toMember).toList();
    }

    @Override
    public Optional<Member> findById(MemberId id) {
        return Optional.ofNullable(memberMapper.findById(id))
            .filter(record -> record.id() != null)
            .map(MemberRecord::toMember);
    }

    @Override
    public void updateStatus(MemberId id, EnrollmentStatus status) {
        memberMapper.updateStatus(id, status);
    }

    @Override
    public List<Member> findMembersByTaskNamesAndStatuses(List<String> taskNames, List<TaskStatus> statuses, int page, int size) {
        int offset = page * size;
        List<MemberRecord> records = memberMapper.findMembersByTaskNamesAndStatuses(taskNames, statuses, offset, size);
        return records.stream()
            .map(MemberRecord::toMember)
            .toList();
    }

}