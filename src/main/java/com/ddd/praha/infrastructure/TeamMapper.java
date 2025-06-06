package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.Team;
import com.ddd.praha.domain.Member;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * チームのMyBatisマッパーインターフェース
 */
@Mapper
public interface TeamMapper {

    /**
     * チームに所属するメンバーを取得する
     * @param teamId チームID
     * @return メンバーのリスト
     */
    @Select("SELECT m.* FROM members m JOIN team_members tm ON m.id = tm.member_id WHERE tm.team_id = #{teamId}")
    List<Member> findMembersByTeamId(@Param("teamId") String teamId);

    /**
     * 全てのチームを取得する
     * @return チームのリスト
     */
    @Select("SELECT id, name FROM teams")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "members", column = "id", javaType = List.class, 
                many = @Many(select = "findMembersByTeamId"))
    })
    List<TeamRecord> findAllRecords();

    /**
     * 全てのチームを取得する
     * @return チームのリスト
     */
    default List<Team> findAll() {
        return findAllRecords().stream()
            .map(TeamRecord::toTeam)
            .toList();
    }

    /**
     * IDでチームを検索する
     * @param id チームID
     * @return チーム
     */
    @Select("SELECT id, name FROM teams WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "members", column = "id", javaType = List.class, 
                many = @Many(select = "findMembersByTeamId"))
    })
    TeamRecord findByIdRecord(@Param("id") String id);

    /**
     * IDでチームを検索する
     * @param id チームID
     * @return チーム
     */
    default Team findById(@Param("id") String id) {
        TeamRecord record = findByIdRecord(id);
        return record != null ? record.toTeam() : null;
    }

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
