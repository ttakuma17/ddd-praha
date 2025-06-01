package com.ddd.praha.infrastructure.persistence.mapper;

import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.MemberName;
import com.ddd.praha.domain.Email;
import com.ddd.praha.domain.EnrollmentStatus;
import com.ddd.praha.infrastructure.persistence.typehandler.MemberIdTypeHandler;
import com.ddd.praha.infrastructure.persistence.typehandler.MemberNameTypeHandler;
import com.ddd.praha.infrastructure.persistence.typehandler.EmailTypeHandler;
import com.ddd.praha.infrastructure.persistence.typehandler.EnrollmentStatusTypeHandler;
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
    @Results({
        @Result(property = "id", column = "id", javaType = MemberId.class, typeHandler = MemberIdTypeHandler.class),
        @Result(property = "name", column = "name", javaType = MemberName.class, typeHandler = MemberNameTypeHandler.class),
        @Result(property = "email", column = "email", javaType = Email.class, typeHandler = EmailTypeHandler.class),
        @Result(property = "status", column = "status", javaType = EnrollmentStatus.class, typeHandler = EnrollmentStatusTypeHandler.class)
    })
    List<Member> findAll();

    /**
     * IDでメンバーを検索する
     * @param id メンバーID
     * @return メンバー
     */
    @Select("SELECT id, name, email, status FROM members WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id", javaType = MemberId.class, typeHandler = MemberIdTypeHandler.class),
        @Result(property = "name", column = "name", javaType = MemberName.class, typeHandler = MemberNameTypeHandler.class),
        @Result(property = "email", column = "email", javaType = Email.class, typeHandler = EmailTypeHandler.class),
        @Result(property = "status", column = "status", javaType = EnrollmentStatus.class, typeHandler = EnrollmentStatusTypeHandler.class)
    })
    Member findById(@Param("id") String id);

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
