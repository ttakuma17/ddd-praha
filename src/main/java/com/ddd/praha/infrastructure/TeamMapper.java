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
     * チームに所属するメンバーレコードを取得する
     * @param teamId チームID
     * @return メンバーレコードのリスト
     */
    @Select("SELECT m.id, m.name, m.email, m.status FROM members m JOIN team_members tm ON m.id = tm.member_id WHERE tm.team_id = #{teamId}")
    List<MemberRecord> findMemberRecordsByTeamId(@Param("teamId") String teamId);
    
    /**
     * チームに所属するメンバーを取得する
     * @param teamId チームID
     * @return メンバーのリスト
     */
    default List<Member> findMembersByTeamId(@Param("teamId") String teamId) {
        return findMemberRecordsByTeamId(teamId).stream()
            .map(MemberRecord::toMember)
            .toList();
    }

    /**
     * 全てのチームの基本情報を取得する
     * @return チームレコードのリスト
     */
    @Select("SELECT id, name FROM teams")
    List<TeamBasicRecord> findAllBasicRecords();
    
    /**
     * 全てのチームを取得する
     * @return チームのリスト
     */
    default List<Team> findAll() {
        return findAllBasicRecords().stream()
            .map(basicRecord -> {
                List<Member> members = findMembersByTeamId(basicRecord.id());
                return new TeamRecord(basicRecord.id(), basicRecord.name(), 
                    members.stream().map(member -> 
                        new MemberRecord(member.getId().value(), member.getName().value(),
                                       member.getEmail().value(), member.getStatus().name())).toList())
                    .toTeam();
            })
            .toList();
    }
    
    /**
     * チームの基本情報レコード
     */
    record TeamBasicRecord(String id, String name) {}


    /**
     * IDでチームの基本情報を検索する
     * @param id チームID
     * @return チーム基本レコード
     */
    @Select("SELECT id, name FROM teams WHERE id = #{id}")
    TeamBasicRecord findByIdBasicRecord(@Param("id") String id);

    /**
     * IDでチームを検索する
     * @param id チームID
     * @return チーム
     */
    default Team findById(@Param("id") String id) {
        TeamBasicRecord basicRecord = findByIdBasicRecord(id);
        if (basicRecord == null) {
            return null;
        }
        List<Member> members = findMembersByTeamId(basicRecord.id());
        return new TeamRecord(basicRecord.id(), basicRecord.name(), 
            members.stream().map(member -> 
                new MemberRecord(member.getId().value(), member.getName().value(),
                               member.getEmail().value(), member.getStatus().name())).toList())
            .toTeam();
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

    /**
     * チームを削除する
     * @param id チームID
     */
    @Delete("DELETE FROM teams WHERE id = #{id}")
    void delete(@Param("id") String id);
}
