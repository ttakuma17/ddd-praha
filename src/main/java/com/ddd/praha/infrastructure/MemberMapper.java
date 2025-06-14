package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.MemberId;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * メンバーのMyBatisマッパーインターフェース
 */
@Mapper
public interface MemberMapper {

    @Select("SELECT id, name, email, status FROM members")
    List<MemberRecord> getAll();

    @Select("SELECT id, name, email, status FROM members WHERE id = #{id.value}")
    MemberRecord findById(@Param("id") MemberId id);

    @Insert("""
        INSERT INTO members (id, name, email, status)
            SELECT #{member.id.value}, #{member.name.value}, #{member.email.value}, #{member.status}
            WHERE NOT EXISTS (
                SELECT 1 FROM members WHERE id = #{member.id.value}
            )
    """)
    void insert(@Param("member") Member member);

    @Update("UPDATE members SET status = #{status} WHERE id = #{id.value}")
    void updateStatus(@Param("id") MemberId id, @Param("status") EnrollmentStatus status);

    @Select("SELECT id, name, email, status FROM members WHERE id = #{id.value}")
    MemberRecord get(@Param("id") MemberId id);
}
