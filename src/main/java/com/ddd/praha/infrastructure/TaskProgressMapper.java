package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.model.MemberId;
import com.ddd.praha.domain.model.TaskId;
import org.apache.ibatis.annotations.*;

/**
 * 参加者課題のMyBatisマッパーインターフェース
 */
@Mapper
public interface TaskProgressMapper {

    /**
     * 参加者IDと課題IDで参加者課題を検索する
     * @param memberId 参加者ID
     * @param taskId 課題ID
     * @return 参加者課題レコード
     */
    @Select("""
        SELECT
            m.id as memberId,
            m.name as memberName,
            m.email as email,
            m.status as memberStatus,
            t.id as taskId,
            t.name as taskName,
            mt.status as taskStatus
        FROM member_tasks mt
        INNER JOIN members m ON mt.member_id = m.id
        INNER JOIN tasks t ON mt.task_id = t.id
        WHERE mt.member_id = #{memberId.value} AND mt.task_id = #{taskId.value}
    """)
    TaskProgressRecord findByMemberAndTaskRecord(
        @Param("memberId") MemberId memberId, 
        @Param("taskId") TaskId taskId
    );

    /**
     * 参加者課題の進捗ステータスを更新する
     * @param memberId 参加者ID
     * @param taskId 課題ID
     * @param status 新しいステータス
     */
    @Update("UPDATE member_tasks SET status = #{status} WHERE member_id = #{memberId.value} AND task_id = #{taskId.value}")
    void updateTaskStatus(
        @Param("memberId") MemberId memberId, 
        @Param("taskId") TaskId taskId,
        @Param("status") String status
    );
}