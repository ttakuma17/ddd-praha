package com.ddd.praha.infrastructure;


import com.ddd.praha.domain.entity.Task;
import com.ddd.praha.domain.model.TaskId;
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
    List<TaskRecord> findAll();

    /**
     * IDで課題を取得する
     * @param id 課題ID
     * @return 課題レコード
     */
    @Select("SELECT id, name FROM tasks WHERE id = #{id.value}")
    TaskRecord get(@Param("id") TaskId id);

    /**
     * 課題を保存する（新規追加）
     */
    @Insert("INSERT INTO tasks (id, name) VALUES (#{task.id.value}, #{task.name.value})")
    void insert(@Param("task") Task task);

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
