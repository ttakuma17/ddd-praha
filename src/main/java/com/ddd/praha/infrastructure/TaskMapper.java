package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.Task;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 課題のMyBatisマッパーインターフェース
 */
@Mapper
public interface TaskMapper {

    /**
     * 全ての課題を取得する
     * @return 課題レコードのリスト
     */
    @Select("SELECT id, name FROM tasks")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name")
    })
    List<TaskRecord> findAllRecords();

    /**
     * 全ての課題を取得する
     * @return 課題のリスト
     */
    default List<Task> findAll() {
        return findAllRecords().stream()
            .map(TaskRecord::toTask)
            .toList();
    }

    /**
     * IDで課題を検索する
     * @param id 課題ID
     * @return 課題レコード
     */
    @Select("SELECT id, name FROM tasks WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name")
    })
    TaskRecord findByIdRecord(@Param("id") String id);

    /**
     * IDで課題を検索する
     * @param id 課題ID
     * @return 課題
     */
    default Task findById(@Param("id") String id) {
        TaskRecord record = findByIdRecord(id);
        return record != null ? record.toTask() : null;
    }

    /**
     * 課題を保存する（新規追加）
     * @param id 課題ID
     * @param name 課題名
     */
    @Insert("INSERT INTO tasks (id, name) VALUES (#{id}, #{name})")
    void insert(@Param("id") String id, @Param("name") String name);

    /**
     * 課題を更新する
     * @param id 課題ID
     * @param name 課題名
     */
    @Update("UPDATE tasks SET name = #{name} WHERE id = #{id}")
    void update(@Param("id") String id, @Param("name") String name);

    /**
     * 課題が存在するか確認する
     * @param id 課題ID
     * @return 存在する場合はtrue
     */
    @Select("SELECT COUNT(*) FROM tasks WHERE id = #{id}")
    boolean exists(@Param("id") String id);
}
