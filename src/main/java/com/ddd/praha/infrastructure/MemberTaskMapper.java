package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.entity.MemberTask;
import com.ddd.praha.domain.model.MemberId;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 参加者課題のMyBatisマッパーインターフェース
 */
@Mapper
public interface MemberTaskMapper {

    // FIXME
    /**
     * 特定の参加者の課題を取得する
     * @return 参加者課題
     */
    @Select("SELECT m.id as member_id, m.name as member_name, m.email, m.status as member_status " +
            "FROM members m WHERE m.id = #{id.value}")
    MemberTaskRecord findByMemberId(@Param("id") MemberId id);

    /**
     * 特定の課題に取り組んでいる全ての参加者の課題を取得する
     * @param taskId 課題ID
     * @return 参加者課題のリスト
     */
    @Select("SELECT DISTINCT m.id as member_id " +
            "FROM members m " +
            "JOIN member_tasks mt ON m.id = mt.member_id " +
            "WHERE mt.task_id = #{taskId}")
    List<MemberTask> findByTaskId(@Param("taskId") String taskId);

    /**
     * 参加者課題を保存する（新規追加）
     * @param memberId 参加者ID
     * @param taskId 課題ID
     * @param status 課題ステータス
     */
    @Insert("INSERT INTO member_tasks (member_id, task_id, status) VALUES (#{memberId}, #{taskId}, #{status})")
    void insert(@Param("memberId") String memberId, @Param("taskId") String taskId, @Param("status") String status);

    /**
     * 参加者課題を更新する
     * @param memberId 参加者ID
     * @param taskId 課題ID
     * @param status 課題ステータス
     */
    @Update("UPDATE member_tasks SET status = #{status} WHERE member_id = #{memberId} AND task_id = #{taskId}")
    void update(@Param("memberId") String memberId, @Param("taskId") String taskId, @Param("status") String status);

    /**
     * 参加者の全ての課題を削除する
     * @param memberId 参加者ID
     */
    @Delete("DELETE FROM member_tasks WHERE member_id = #{memberId}")
    void deleteAllByMemberId(@Param("memberId") String memberId);

    /**
     * 指定された課題群が指定されたステータスになっている参加者をページングして取得する
     * @param taskIds 課題IDのリスト
     * @param statuses ステータスのリスト
     * @param offset オフセット
     * @param limit 取得件数
     * @return 条件に合致する参加者のリスト
     */
    @Select("<script>" +
            "SELECT DISTINCT m.id, m.name, m.email, m.status " +
            "FROM members m " +
            "WHERE m.id IN (" +
            "  SELECT mt.member_id " +
            "  FROM member_tasks mt " +
            "  WHERE mt.task_id IN " +
            "  <foreach collection='taskIds' item='taskId' open='(' separator=',' close=')'>" +
            "    #{taskId}" +
            "  </foreach>" +
            "  AND mt.status IN " +
            "  <foreach collection='statuses' item='status' open='(' separator=',' close=')'>" +
            "    #{status}" +
            "  </foreach>" +
            "  GROUP BY mt.member_id " +
            "  HAVING COUNT(DISTINCT mt.task_id) = #{taskIds.size()}" +
            ") " +
            "ORDER BY m.name " +
            "LIMIT #{limit} OFFSET #{offset}" +
            "</script>")
    List<MemberRecord> findMemberRecordsByTasksAndStatuses(@Param("taskIds") List<String> taskIds,
                                                           @Param("statuses") List<String> statuses, 
                                                           @Param("offset") int offset, 
                                                           @Param("limit") int limit);

    default List<Member> findMembersByTasksAndStatuses(List<String> taskIds, List<String> statuses, int offset, int limit) {
        return findMemberRecordsByTasksAndStatuses(taskIds, statuses, offset, limit)
            .stream()
            .map(MemberRecord::toMember)
            .collect(Collectors.toList());
    }

    /**
     * 指定された課題群が指定されたステータスになっている参加者の総数を取得する
     * @param taskIds 課題IDのリスト
     * @param statuses ステータスのリスト
     * @return 条件に合致する参加者の総数
     */
    @Select("<script>" +
            "SELECT COUNT(DISTINCT m.id) " +
            "FROM members m " +
            "WHERE m.id IN (" +
            "  SELECT mt.member_id " +
            "  FROM member_tasks mt " +
            "  WHERE mt.task_id IN " +
            "  <foreach collection='taskIds' item='taskId' open='(' separator=',' close=')'>" +
            "    #{taskId}" +
            "  </foreach>" +
            "  AND mt.status IN " +
            "  <foreach collection='statuses' item='status' open='(' separator=',' close=')'>" +
            "    #{status}" +
            "  </foreach>" +
            "  GROUP BY mt.member_id " +
            "  HAVING COUNT(DISTINCT mt.task_id) = #{taskIds.size()}" +
            ")" +
            "</script>")
    long countMembersByTasksAndStatuses(@Param("taskIds") List<String> taskIds, 
                                        @Param("statuses") List<String> statuses);
}
