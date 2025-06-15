package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.MemberId;
import com.ddd.praha.domain.model.TaskStatus;
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

    @Select("""
        SELECT DISTINCT m.id, m.name, m.email, m.status
        FROM members m
        INNER JOIN member_tasks mt ON m.id = mt.member_id
        INNER JOIN tasks t ON mt.task_id = t.id
        WHERE t.name IN
        <foreach item="taskName" collection="taskNames" open="(" separator="," close=")">
            #{taskName}
        </foreach>
        AND mt.status IN
        <foreach item="status" collection="statuses" open="(" separator="," close=")">
            #{status}
        </foreach>
        ORDER BY m.id
        LIMIT #{limit} OFFSET #{offset}
    """)
    List<MemberRecord> findMembersByTaskNamesAndStatuses(
        @Param("taskNames") List<String> taskNames,
        @Param("statuses") List<TaskStatus> statuses,
        @Param("offset") int offset,
        @Param("limit") int limit
    );
}
