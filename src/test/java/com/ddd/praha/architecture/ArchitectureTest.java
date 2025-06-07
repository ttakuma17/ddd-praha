package com.ddd.praha.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * アーキテクチャの依存関係を検証するテスト
 * 
 * このプロジェクトでは以下の依存関係を守る必要があります：
 * - application, infrastructure, presentationはdomainに依存してよい
 * - domainは他のどのレイヤーにも依存してはいけない
 * 
 * このテストクラスはArchUnitを使用して、上記の依存関係ルールが守られているかを検証します。
 * ArchUnitはコードの静的解析を行い、アーキテクチャ上の制約違反を検出するためのライブラリです。
 * 
 * テストの内容：
 * 1. ドメイン層が他のレイヤーに依存していないことを確認するテスト
 *    - domainShouldNotDependOnApplication
 *    - domainShouldNotDependOnInfrastructure
 *    - domainShouldNotDependOnPresentation
 * 
 * 2. 各レイヤーが適切なレイヤーにのみ依存していることを確認するテスト
 *    - applicationMayDependOnDomain
 *    - infrastructureMayDependOnDomainAndApplication
 *    - presentationMayDependOnDomainAndApplication
 * 
 * これらのテストが失敗した場合、アーキテクチャの依存関係に違反があることを示します。
 * エラーメッセージには、どのクラスがどのルールに違反しているかが詳細に表示されます。
 */
public class ArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter()
                .withImportOption(location -> !location.contains("/test/"))
                .importPackages("com.ddd.praha");
    }

    @Test
    @DisplayName("ドメイン層はアプリケーション層に依存してはいけない")
    public void domainShouldNotDependOnApplication() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..application..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("ドメイン層はインフラストラクチャ層に依存してはいけない")
    public void domainShouldNotDependOnInfrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("ドメイン層はプレゼンテーション層に依存してはいけない")
    public void domainShouldNotDependOnPresentation() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..presentation..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("アプリケーション層はドメイン層に依存してよい")
    public void applicationMayDependOnDomain() {
        ArchRule rule = classes()
                .that().resideInAPackage("..application..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "..domain..",
                        "..application..",
                        "java..",
                        "javax..",
                        "org.springframework..",
                        "org.slf4j..",
                        "com.fasterxml..",
                        "org.apache..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("インフラストラクチャ層はドメイン層とアプリケーション層に依存してよい")
    public void infrastructureMayDependOnDomainAndApplication() {
        ArchRule rule = classes()
                .that().resideInAPackage("..infrastructure..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "..domain..",
                        "..application..",
                        "..infrastructure..",
                        "java..",
                        "javax..",
                        "jakarta..",
                        "org.springframework..",
                        "org.slf4j..",
                        "com.fasterxml..",
                        "org.apache..",
                        "org.mybatis..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("プレゼンテーション層はドメイン層とアプリケーション層に依存してよい")
    public void presentationMayDependOnDomainAndApplication() {
        ArchRule rule = classes()
                .that().resideInAPackage("..presentation..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "..domain..",
                        "..application..",
                        "..presentation..",
                        "java..",
                        "javax..",
                        "jakarta..",
                        "org.springframework..",
                        "org.slf4j..",
                        "com.fasterxml..",
                        "org.apache..");

        rule.check(importedClasses);
    }
}
