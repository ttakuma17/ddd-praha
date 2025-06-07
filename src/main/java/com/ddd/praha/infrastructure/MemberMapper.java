package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.Member;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;

import java.util.List;

/**
 * メンバーのMyBatisマッパーインターフェース
 */
@Mapper
public interface MemberMapper {

    /**
     * 全てのメンバーを取得する
     * @return メンバーのリスト
     */
    @Select("SELECT id, name, email, status FROM members")
    List<MemberRecord> findAllRecords();

    /**
     * 全てのメンバーを取得する
     * @return メンバーのリスト
     */
    default List<Member> findAll() {
        return findAllRecords().stream()
            .map(MemberRecord::toMember)
            .toList();
    }

    /**
     * IDでメンバーを検索する
     * @param id メンバーID
     * @return メンバー
     */
    @Select("SELECT id, name, email, status FROM members WHERE id = #{id}")
    MemberRecord findByIdRecord(@Param("id") String id);

    /**
     * IDでメンバーを検索する
     * @param id メンバーID
     * @return メンバー
     */
    default Member findById(@Param("id") String id) {
        MemberRecord record = findByIdRecord(id);
        return record != null ? record.toMember() : null;
    }

    /**
     * メンバーを保存する（新規追加）
     * @param id メンバーID
     * @param name メンバー名
     * @param email メールアドレス
     * @param status 受講ステータス
     */
    @Insert("INSERT INTO members (id, name, email, status) VALUES (#{id}, #{name}, #{email}, #{status})")
    void insert(@Param("id") String id, @Param("name") String name, @Param("email") String email, @Param("status") String status);

    /**
     * メンバーを更新する
     * @param id メンバーID
     * @param name メンバー名
     * @param email メールアドレス
     * @param status 受講ステータス
     */
    @Update("UPDATE members SET name = #{name}, email = #{email}, status = #{status} WHERE id = #{id}")
    void update(@Param("id") String id, @Param("name") String name, @Param("email") String email, @Param("status") String status);

    /**
     * メンバーが存在するか確認する
     * @param id メンバーID
     * @return 存在する場合はtrue
     */
    @Select("SELECT COUNT(*) FROM members WHERE id = #{id}")
    boolean exists(@Param("id") String id);
}
