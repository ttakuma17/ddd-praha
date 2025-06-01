package com.ddd.praha.infrastructure.persistence.mapper;

import com.ddd.praha.domain.Task;
import com.ddd.praha.domain.TaskId;
import com.ddd.praha.domain.TaskName;
import com.ddd.praha.infrastructure.persistence.typehandler.TaskIdTypeHandler;
import com.ddd.praha.infrastructure.persistence.typehandler.TaskNameTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 課題のMyBatisマッパーインターフェース
 */
@Mapper
public interface TaskMapper {
    
    /**
     * 全ての課題を取得する
     * @return 課題のリスト
     */
    @Select("SELECT id, name FROM tasks")
    @Results({
        @Result(property = "id", column = "id", javaType = TaskId.class, typeHandler = TaskIdTypeHandler.class),
        @Result(property = "name", column = "name", javaType = TaskName.class, typeHandler = TaskNameTypeHandler.class)
    })
    List<Task> findAll();
    
    /**
     * IDで課題を検索する
     * @param id 課題ID
     * @return 課題
     */
    @Select("SELECT id, name FROM tasks WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id", javaType = TaskId.class, typeHandler = TaskIdTypeHandler.class),
        @Result(property = "name", column = "name", javaType = TaskName.class, typeHandler = TaskNameTypeHandler.class)
    })
    Task findById(@Param("id") String id);
    
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