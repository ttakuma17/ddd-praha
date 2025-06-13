package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import org.apache.ibatis.annotations.*;

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
    List<MemberRecord> getAll();

    /**
     * IDでメンバーを検索する
     * @param id メンバーID
     * @return メンバー
     */
    @Select("SELECT id, name, email, status FROM members WHERE id = #{id}")
    MemberRecord findById(@Param("id") String id);

    /**
     * メンバーを保存する（新規追加）
     * @param id メンバーID
     * @param name メンバー名
     * @param email メールアドレス
     * @param status 受講ステータス
     */
    @Insert("""
        INSERT INTO members (id, name, email, status)
            SELECT #{id}, #{name}, #{email}, #{status}
            WHERE NOT EXISTS (
                SELECT 1 FROM members WHERE id = #{id}
            )
    """)
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

    @Select("SELECT id, name, email, status FROM members WHERE id = #{id}")
    MemberRecord get(MemberId id);
}
