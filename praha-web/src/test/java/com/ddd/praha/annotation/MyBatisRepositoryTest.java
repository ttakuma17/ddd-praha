package com.ddd.praha.annotation;

import com.ddd.praha.TestcontainersConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MyBatisリポジトリテスト用のカスタムアノテーション
 * 
 * このアノテーションは以下の機能を提供します：
 * - SpringBootテストコンテキストの設定（MyBatisサポート含む）
 * - Testcontainersを使用したPostgreSQLコンテナの起動
 * - トランザクション管理とテスト後の自動ロールバック
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
@Rollback
public @interface MyBatisRepositoryTest {
}