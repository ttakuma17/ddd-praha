package com.ddd.praha.infrastructure.persistence.mapper;

import com.ddd.praha.domain.Team;
import com.ddd.praha.domain.TeamId;
import com.ddd.praha.domain.TeamName;
import com.ddd.praha.domain.Member;
import com.ddd.praha.infrastructure.persistence.typehandler.TeamIdTypeHandler;
import com.ddd.praha.infrastructure.persistence.typehandler.TeamNameTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * チームのMyBatisマッパーインターフェース
 */
@Mapper
public interface TeamMapper {
    
    /**
     * 全てのチームを取得する
     * @return チームのリスト
     */
    @Select("SELECT id, name FROM teams")
    @Results({
        @Result(property = "id", column = "id", javaType = TeamId.class, typeHandler = TeamIdTypeHandler.class),
        @Result(property = "name", column = "name", javaType = TeamName.class, typeHandler = TeamNameTypeHandler.class),
        @Result(property = "list", column = "id", javaType = List.class, 
                many = @Many(select = "findMembersByTeamId"))
    })
    List<Team> findAll();
    
    /**
     * IDでチームを検索する
     * @param id チームID
     * @return チーム
     */
    @Select("SELECT id, name FROM teams WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id", javaType = TeamId.class, typeHandler = TeamIdTypeHandler.class),
        @Result(property = "name", column = "name", javaType = TeamName.class, typeHandler = TeamNameTypeHandler.class),
        @Result(property = "list", column = "id", javaType = List.class, 
                many = @Many(select = "findMembersByTeamId"))
    })
    Team findById(@Param("id") String id);
    
    /**
     * チームに所属するメンバーを取得する
     * @param teamId チームID
     * @return メンバーのリスト
     */
    @Select("SELECT m.* FROM members m JOIN team_members tm ON m.id = tm.member_id WHERE tm.team_id = #{teamId}")
    List<Member> findMembersByTeamId(@Param("teamId") String teamId);
    
    /**
     * チームを保存する（新規追加）
     * @param id チームID
     * @param name チーム名
     */
    @Insert("INSERT INTO teams (id, name) VALUES (#{id}, #{name})")
    void insert(@Param("id") String id, @Param("name") String name);
    
    /**
     * チームを更新する
     * @param id チームID
     * @param name チーム名
     */
    @Update("UPDATE teams SET name = #{name} WHERE id = #{id}")
    void update(@Param("id") String id, @Param("name") String name);
    
    /**
     * チームメンバーを追加する
     * @param teamId チームID
     * @param memberId メンバーID
     */
    @Insert("INSERT INTO team_members (team_id, member_id) VALUES (#{teamId}, #{memberId})")
    void addMember(@Param("teamId") String teamId, @Param("memberId") String memberId);
    
    /**
     * チームメンバーを削除する
     * @param teamId チームID
     * @param memberId メンバーID
     */
    @Delete("DELETE FROM team_members WHERE team_id = #{teamId} AND member_id = #{memberId}")
    void removeMember(@Param("teamId") String teamId, @Param("memberId") String memberId);
    
    /**
     * チームのメンバーを全て削除する
     * @param teamId チームID
     */
    @Delete("DELETE FROM team_members WHERE team_id = #{teamId}")
    void removeAllMembers(@Param("teamId") String teamId);
    
    /**
     * チームが存在するか確認する
     * @param id チームID
     * @return 存在する場合はtrue
     */
    @Select("SELECT COUNT(*) FROM teams WHERE id = #{id}")
    boolean exists(@Param("id") String id);
}