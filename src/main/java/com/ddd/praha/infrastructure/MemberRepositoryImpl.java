package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.application.repository.MemberRepository;
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
        return Optional.ofNullable(memberMapper.findById(id.value()))
            .filter(record -> record.id() != null)
            .map(MemberRecord::toMember);
    }

    @Override
    public void save(Member member) {
        memberMapper.insert(
                member.getId().value(),
                member.getName().value(),
                member.getEmail().value(),
                member.getStatus().name()
        );
    }

    @Override
    public void update(Member member) {
        memberMapper.update(member);
    }
}