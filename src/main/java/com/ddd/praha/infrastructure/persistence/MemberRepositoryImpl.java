package com.ddd.praha.infrastructure.persistence;

import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.application.repository.MemberRepository;
import com.ddd.praha.infrastructure.persistence.mapper.MemberMapper;
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
    public List<Member> findAll() {
        return memberMapper.findAll();
    }

    @Override
    public Optional<Member> findById(MemberId id) {
        Member member = memberMapper.findById(id.value());
        return Optional.ofNullable(member);
    }

    @Override
    public Member save(Member member) {
        if (memberMapper.exists(member.getId().value())) {
            memberMapper.update(
                member.getId().value(),
                member.getName().value(),
                member.getEmail().value(),
                member.getStatus().name()
            );
        } else {
            memberMapper.insert(
                member.getId().value(),
                member.getName().value(),
                member.getEmail().value(),
                member.getStatus().name()
            );
        }
        return member;
    }
}