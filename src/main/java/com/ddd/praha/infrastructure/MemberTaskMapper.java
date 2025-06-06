package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.MemberTask;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.Task;
import com.ddd.praha.domain.TaskStatus;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 参加者課題のMyBatisマッパーインターフェース
 */
@Mapper
public interface MemberTaskMapper {

    /**
     * 特定の参加者の課題を取得する
     * @param memberId 参加者ID
     * @return 参加者課題
     */
    @Select("SELECT m.id as member_id, m.name as member_name, m.email, m.status as member_status " +
            "FROM members m WHERE m.id = #{memberId}")
    @Results({
        @Result(property = "owner", column = "member_id", javaType = Member.class, 
                one = @One(select = "com.ddd.praha.infrastructure.MemberMapper.findById")),
        @Result(property = "map", column = "member_id", javaType = Map.class, 
                many = @Many(select = "findTaskStatusMapByMemberId"))
    })
    MemberTask findByMemberId(@Param("memberId") String memberId);

    /**
     * 特定の課題に取り組んでいる全ての参加者の課題を取得する
     * @param taskId 課題ID
     * @return 参加者課題のリスト
     */
    @Select("SELECT DISTINCT m.id as member_id " +
            "FROM members m " +
            "JOIN member_tasks mt ON m.id = mt.member_id " +
            "WHERE mt.task_id = #{taskId}")
    @Results({
        @Result(property = "owner", column = "member_id", javaType = Member.class, 
                one = @One(select = "com.ddd.praha.infrastructure.MemberMapper.findById")),
        @Result(property = "map", column = "member_id", javaType = Map.class, 
                many = @Many(select = "findTaskStatusMapByMemberId"))
    })
    List<MemberTask> findByTaskId(@Param("taskId") String taskId);

    /**
     * 参加者の課題ステータスレコードを取得する
     * @param memberId 参加者ID
     * @return 課題ステータスレコードのリスト
     */
    @Select("SELECT #{memberId} as member_id, t.id as task_id, t.name as task_name, mt.status " +
            "FROM tasks t " +
            "LEFT JOIN member_tasks mt ON t.id = mt.task_id AND mt.member_id = #{memberId}")
    @Results({
        @Result(property = "memberId", column = "member_id"),
        @Result(property = "taskId", column = "task_id"),
        @Result(property = "status", column = "status")
    })
    List<MemberTaskRecord> findTaskStatusRecordsByMemberId(@Param("memberId") String memberId);

    /**
     * 参加者の課題ステータスマップを取得する
     * @param memberId 参加者ID
     * @return 課題とステータスのマップ
     */
    default Map<Task, TaskStatus> findTaskStatusMapByMemberId(@Param("memberId") String memberId) {
        List<MemberTaskRecord> records = findTaskStatusRecordsByMemberId(memberId);
        Map<Task, TaskStatus> result = new HashMap<>();

        for (MemberTaskRecord record : records) {
            Task task = new TaskRecord(
                record.taskId(), 
                ""  // We don't have task name here, but it's not used in the map key comparison
            ).toTask();

            result.put(task, record.toTaskStatus());
        }

        return result;
    }

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
     * 参加者課題が存在するか確認する
     * @param memberId 参加者ID
     * @param taskId 課題ID
     * @return 存在する場合はtrue
     */
    @Select("SELECT COUNT(*) FROM member_tasks WHERE member_id = #{memberId} AND task_id = #{taskId}")
    boolean exists(@Param("memberId") String memberId, @Param("taskId") String taskId);
}
