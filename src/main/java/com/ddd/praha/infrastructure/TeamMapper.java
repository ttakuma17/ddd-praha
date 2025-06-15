package com.ddd.praha.infrastructure;


import com.ddd.praha.domain.entity.Team;
import com.ddd.praha.domain.model.MemberId;
import com.ddd.praha.domain.model.TeamId;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * チームのMyBatisマッパーインターフェース
 */
@Mapper
public interface TeamMapper {

  @Select("""
          SELECT
             t.id as team_id,
             t.name as team_name,
             m.id as member_id,
             m.name as member_name,
             m.email as member_email,
             m.status as member_status
          FROM
              teams t
              LEFT JOIN team_members tm ON t.id = tm.team_id
              LEFT JOIN members m ON tm.member_id = m.id
      """)
  List<TeamMemberJoinRecord> getAllWithMembers();

  /**
   * IDでチームの基本情報を検索する
   *
   * @param id チームID
   * @return チームレコード
   */
  @Select("""
          SELECT
             t.id as team_id,
             t.name as team_name,
             m.id as member_id,
             m.name as member_name,
             m.email as member_email,
             m.status as member_status
          FROM
              teams t
              LEFT JOIN team_members tm ON t.id = tm.team_id
              LEFT JOIN members m ON tm.member_id = m.id
          WHERE
              t.id = #{id.value}
      """)
  List<TeamMemberJoinRecord> getWithMembers(@Param("id") TeamId id);

  /**
   * チームを保存する（新規追加）
   */
  @Insert("INSERT INTO teams (id, name) VALUES (#{team.id.value}, #{team.name.value})")
  void insert(@Param("team") Team team);

  /**
   * チームを更新する
   *
   * @param id   チームID
   * @param name チーム名
   */
  @Update("UPDATE teams SET name = #{name} WHERE id = #{id}")
  void update(@Param("id") String id, @Param("name") String name);

  /**
   * チームメンバーを追加する
   *
   * @param teamId   チームID
   * @param memberId メンバーID
   */
  @Insert("INSERT INTO team_members (team_id, member_id) VALUES (#{teamId.value}, #{memberId.value})")
  void addMember(@Param("teamId") TeamId teamId, @Param("memberId") MemberId memberId);

  /**
   * チームのメンバーを全て削除する
   *
   * @param teamId チームID
   */
  @Delete("DELETE FROM team_members WHERE team_id = #{teamId.value}")
  void removeAllMembers(@Param("teamId") TeamId teamId);

  /**
   * チームが存在するか確認する
   *
   * @param id チームID
   * @return 存在する場合はtrue
   */
  @Select("SELECT COUNT(*) FROM teams WHERE id = #{id.value}")
  boolean exists(@Param("id") TeamId id);

  @Select("SELECT id, name FROM teams WHERE name = #{name}")
  TeamRecord findByName(@Param("name") String name);

  /**
   * チームを削除する
   *
   * @param id チームID
   */
  @Delete("DELETE FROM teams WHERE id = #{id.value}")
  void delete(@Param("id") TeamId id);

  @Delete("DELETE FROM team_members WHERE team_id = #{teamId.value} AND member_id = #{memberId.value}")
  void removeMember(@Param("teamId") TeamId teamId, @Param("memberId") MemberId memberId);

  @Insert("""
          <script>
          INSERT INTO team_members (team_id, member_id)
          VALUES
          <foreach collection="memberIds" item="memberId" separator=",">
            (#{teamId.value}, #{memberId.value})
          </foreach>
          </script>
      """)
  void addMembers(@Param("teamId") TeamId id, @Param("memberIds") List<MemberId> memberIds);
}
