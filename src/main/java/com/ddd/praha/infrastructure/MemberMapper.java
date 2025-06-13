package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * メンバーのMyBatisマッパーインターフェース
 */
@Mapper
public interface MemberMapper {

    @Select("SELECT id, name, email, status FROM members")
    List<MemberRecord> getAll();

    @Select("SELECT id, name, email, status FROM members WHERE id = #{id}")
    MemberRecord findById(@Param("id") String id);

    @Insert("""
        INSERT INTO members (id, name, email, status)
            SELECT #{id}, #{name}, #{email}, #{status}
            WHERE NOT EXISTS (
                SELECT 1 FROM members WHERE id = #{id}
            )
    """)
    void insert(@Param("id") String id, @Param("name") String name, @Param("email") String email, @Param("status") String status);

    @Update("UPDATE members SET status = #{member.status} WHERE id = #{member.id.value}")
    void updateStatus(@Param("member") Member member);

    @Select("SELECT id, name, email, status FROM members WHERE id = #{id}")
    MemberRecord get(MemberId id);
}
