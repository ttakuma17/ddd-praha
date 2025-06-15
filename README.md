# Praha DDD プロジェクト

学習組織における参加者・チーム・課題管理システムです。ドメイン駆動設計（DDD）とオニオンアーキテクチャに基づいて構築されています。

## 📋 プロジェクト概要

このシステムは以下の機能を提供します：

- **参加者管理**: 学習者の登録・在籍状況管理
- **チーム管理**: 2-4人のチーム編成・メンバー管理
- **課題管理**: 学習課題の進捗追跡・ステータス管理
- **検索機能**: 課題名による参加者検索

## 🛠 技術スタック

- **言語**: Java 21
- **フレームワーク**: Spring Boot 3.4.6
- **データベース**: PostgreSQL (本番), Testcontainers PostgreSQL (テスト)
- **ORM**: MyBatis (アノテーション方式、XMLマッパー不使用)
- **マイグレーション**: Flyway
- **ビルドツール**: Gradle
- **テスト**: JUnit 5, Testcontainers, ArchUnit
- **メッセージキュー**: RabbitMQ
- **API文書**: SpringDoc OpenAPI (Swagger UI)
- **CI/CD**: GitHub Actions
- **コード品質**: JaCoCo (カバレッジ), Checkstyle, SpotBugs
- **依存関係管理**: Dependabot

## 🏗 アーキテクチャ

### 設計哲学

- **オニオンアーキテクチャ**: 依存関係の方向を制御し、ドメインを中心とした設計
- **ドメイン駆動設計（DDD）**: ビジネスロジックをドメイン層に集約
- **テスト駆動開発（TDD）**: テストファーストで開発を進める

### レイヤー構成

```
src/main/java/com/ddd/praha/
├── domain/          # ドメイン層（エンティティ、値オブジェクト、ビジネスルール）
├── application/     # アプリケーション層（ユースケース、リポジトリインターフェース）
├── infrastructure/ # インフラストラクチャ層（リポジトリ実装、外部I/F）
└── presentation/   # プレゼンテーション層（REST API、コントローラー）
```

## 📊 ドメインモデル

### エンティティ

- **Member（参加者）**: 名前、メール、在籍ステータスを持つ学習者
- **Team（チーム）**: 2-4人のメンバーで構成されるグループ
- **Task（課題）**: 学習課題
- **MemberTask（参加者課題）**: 参加者の課題進捗を管理

### 値オブジェクト

- **ID系**: MemberId, TeamId, TaskId (UUID)
- **名前系**: MemberName (30文字以内), TeamName (20文字以内)
- **Email**: 形式検証済みメールアドレス
- **EnrollmentStatus**: 在籍ステータス（在籍中、休会中、退会済）
- **TaskStatus**: 課題ステータス（未着手、取組中、レビュー待ち、完了）

### ビジネスルール

1. チームは2-4人のメンバーで構成される
2. 在籍中（在籍中）のメンバーのみチームに参加可能
3. 課題ステータスの遷移ルールが定義されている
4. 在籍ステータスの変更時は自動的にチーム再編成が実行される

## 📁 プロジェクト構成

```
src/
├── main/
│   ├── java/com/ddd/praha/
│   │   ├── domain/          # ドメイン層
│   │   ├── application/     # アプリケーション層
│   │   ├── infrastructure/  # インフラストラクチャ層
│   │   └── presentation/    # プレゼンテーション層
│   └── resources/
│       ├── db/
│       │   ├── migration/   # Flywayマイグレーション
│       │   └── local/       # 開発用テストデータ
│       ├── application.yml  # 本番設定
│       └── application-dev.yml # 開発設定
└── test/
    ├── java/                # テストコード
    └── resources/
        └── application-test.yml # テスト設定
```

## 🚀 セットアップ・実行方法

### 必要な環境

- Java 21
- Docker（Testcontainers使用のため）
- Gradle

### 開発環境での実行

```bash
# Testcontainersを使用してアプリケーションを起動（推奨）
./gradlew bootTestRun

# 本番環境同様の起動（PostgreSQLが必要）
./gradlew bootRun
```

### テスト実行

```bash
# 全テスト実行
./gradlew test

# 特定のテストクラス実行
./gradlew test --tests "MemberServiceTest"

# カバレッジレポート付きテスト実行
./gradlew test jacocoTestReport

# コード品質チェック実行
./gradlew check

# ビルド
./gradlew build

# クリーンビルド
./gradlew clean build
```

### API文書の確認

アプリケーション起動後、以下のURLでAPI文書を確認できます：
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## 🌐 API エンドポイント

### 参加者管理
- `GET /api/members` - 参加者一覧取得
- `GET /api/members/{id}` - 参加者詳細取得
- `POST /api/members` - 参加者新規作成
- `PUT /api/members/{id}/status` - 参加者ステータス更新

### チーム管理
- `GET /api/teams` - チーム一覧取得
- `GET /api/teams/{id}` - チーム詳細取得
- `PUT /api/teams/{id}/members` - チームメンバー更新

### 課題管理
- `GET /api/tasks` - 課題一覧取得
- `PUT /api/tasks/{taskId}/members/{memberId}/status` - 課題ステータス更新

### 検索機能
- `POST /api/search/members` - 課題名による参加者検索

## 🗄 データベース構造

### 主要テーブル

- **members**: 参加者情報
- **teams**: チーム情報
- **tasks**: 課題情報
- **team_members**: チーム-参加者関連
- **member_tasks**: 参加者-課題進捗管理

詳細なERDは `.claude/domain-erd.mmd` を参照してください。

## 🧪 テスト戦略

### テスト種別

- **単体テスト**: ドメインロジック、値オブジェクトのテスト
- **統合テスト**: API、リポジトリのテスト（Testcontainers使用）
- **アーキテクチャテスト**: ArchUnitによる依存関係検証

### テスト設定

- テスト環境では `application-test.yml` が使用される
- Testcontainers により PostgreSQL と RabbitMQ が自動起動
- テストデータは Flyway マイグレーション `R__Insert_test_data.sql` で投入

### コード品質管理

- **JaCoCo**: テストカバレッジ測定（目標: 80%以上）
- **Checkstyle**: コードスタイルチェック
- **SpotBugs**: 静的解析によるバグ検出

カバレッジレポートは `build/reports/jacoco/test/html/index.html` で確認できます。

## 🚀 CI/CD

### GitHub Actions

プロジェクトでは以下のGitHub Actionsワークフローが設定されています：

- **CI (`ci.yml`)**: プッシュ・プルリクエスト時の自動テスト実行
- **Code Quality (`quality.yml`)**: コード品質チェック・カバレッジ測定
- **PR Check (`pr-check.yml`)**: プルリクエスト時の高速チェック

### Dependabot

依存関係の自動更新が設定されています：

- **Gradle依存関係**: 毎週月曜日 9:00 JST に自動チェック
- **GitHub Actions**: ワークフローで使用するアクションの自動更新
- **セキュリティアップデート**: 脆弱性発見時の自動プルリクエスト作成

### バッジ表示

以下のバッジをREADMEに追加することを推奨します：

```markdown
![CI](https://github.com/ttakuma17/praha/workflows/CI/badge.svg)
![Code Quality](https://github.com/ttakuma17/praha/workflows/Code%20Quality/badge.svg)
[![codecov](https://codecov.io/gh/ttakuma17/praha/branch/main/graph/badge.svg)](https://codecov.io/gh/ttakuma17/praha)
```

## 📝 開発ガイドライン

### コーディング規約

- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) に準拠
- リフレクションの使用は禁止
- 日本語でのコメント・ドキュメント作成

### TDD原則

1. まずテストを作成する
2. テストの失敗を確認する
3. 最小限の実装でテストを通す
4. リファクタリングする
5. このサイクルを繰り返す

### レイヤー責務

- **ドメイン層**: ビジネスロジックの実装
- **アプリケーション層**: ユースケースの調整（ロジック最小限）
- **インフラストラクチャ層**: 外部システムとの連携
- **プレゼンテーション層**: APIの入出力制御

## 🔧 トラブルシューティング

### よくある問題

1. **Dockerが起動しない**
   - Docker Desktopが実行されているか確認
   - Testcontainersはコンテナ起動にDockerが必要

2. **テストデータが投入されない**
   - `application-dev.yml` で `spring.flyway.locations` に `classpath:db/local` が含まれているか確認
   - Repeatable migration `R__` プレフィックスが正しいか確認

3. **ビルドエラー**
   - Java 21が正しくインストールされているか確認
   - `./gradlew clean build` でクリーンビルドを試行

4. **API呼び出しエラー**
   - Swagger UIで正しいリクエスト形式を確認
   - ログレベルをDEBUGに設定して詳細確認

### ログ確認

```bash
# アプリケーションログ
tail -f logs/application.log

# デバッグモードでの起動
SPRING_PROFILES_ACTIVE=dev ./gradlew bootTestRun
```

## 🔮 今後の開発予定

### Phase 1: メッセージング基盤
- [ ] **RabbitMQ Consumer サーバの実装**
  - [ ] 独立したConsumerアプリケーションの作成
  - [ ] チーム通知メッセージの処理ロジック実装
  - [ ] Dead Letter Queueの設定
  - [ ] Consumer のヘルスチェック機能

### Phase 2: オブザーバビリティ基盤
- [ ] **OpenTelemetry統合**
  - [ ] Spring Boot OpenTelemetry Auto-instrumentationの導入
  - [ ] トレーシング（分散トレース）の実装
  - [ ] メトリクス収集の設定
  - [ ] ログ相関IDの実装

- [ ] **Micrometer + OSS監視スタック**
  - [ ] Prometheus（メトリクス収集）
  - [ ] Grafana（可視化・ダッシュボード）
  - [ ] Jaeger（分散トレーシング）
  - [ ] Loki（ログ集約）

- [ ] **監視・アラート**
  - [ ] アプリケーションメトリクス（レスポンス時間、エラー率等）
  - [ ] ビジネスメトリクス（ユーザー数、チーム数等）
  - [ ] SLI/SLO定義とアラートルール
  - [ ] ダッシュボードテンプレート作成

### Phase 3: ローカル開発環境（K8s + minikube）
- [ ] **Kubernetes マニフェスト**
  - [ ] Deployment, Service, ConfigMap, Secret
  - [ ] HorizontalPodAutoscaler（HPA）
  - [ ] Ingress設定
  - [ ] Namespace分割（dev, staging等）

- [ ] **minikube ローカル環境**
  - [ ] minikube セットアップスクリプト
  - [ ] Helm Chart作成
  - [ ] Skaffold設定（開発効率化）
  - [ ] ローカルPersistentVolume設定

- [ ] **依存サービスのK8s化**
  - [ ] PostgreSQL (StatefulSet)
  - [ ] RabbitMQ (StatefulSet)
  - [ ] Redis（キャッシュ層として追加予定）

- [ ] **CI/CD with K8s**
  - [ ] GitHub Actions + minikube
  - [ ] Docker image build & push
  - [ ] K8s rolling update
  - [ ] E2E test in K8s environment

### Phase 4: マイクロサービス学習機能
- [ ] **コンテンツ配信サービス（CDN統合）**
  - [ ] 課題資料・動画のアップロード機能
  - [ ] AWS S3/CloudFront連携
  - [ ] Resilience4j Circuit Breaker（外部API障害対応）
  - [ ] Retry + Fallback パターン実装

- [ ] **外部学習プラットフォーム連携**
  - [ ] GitHub API統合（課題提出状況取得）
  - [ ] Slack API統合（通知・Bot機能）
  - [ ] Zoom API統合（勉強会スケジュール管理）
  - [ ] Rate Limiting + Bulkhead パターン

- [ ] **推薦・分析エンジン**
  - [ ] 学習進捗に基づく課題推薦
  - [ ] 機械学習API呼び出し（推薦アルゴリズム）
  - [ ] Time Limiter + Timeout制御
  - [ ] 分析結果キャッシュ戦略

- [ ] **通知・コミュニケーション基盤**
  - [ ] リアルタイム通知（WebSocket/SSE）
  - [ ] メール配信サービス（SendGrid/SES）
  - [ ] プッシュ通知（FCM）
  - [ ] 配信失敗時のFallback chain

### Phase 5: サービス分離・マイクロサービス化
- [ ] **Core Domain Service**
  - [ ] Member Management Service
  - [ ] Team Orchestration Service  
  - [ ] Task Progress Service
  - [ ] Service Mesh（Istio/Linkerd）導入

- [ ] **Supporting Services**
  - [ ] Notification Service（メール・プッシュ通知）
  - [ ] File Storage Service（課題資料管理）
  - [ ] Analytics Service（学習分析・レポート）
  - [ ] Content Recommendation Service

- [ ] **マイクロサービス間通信**
  - [ ] Synchronous: OpenFeign + Resilience4j
  - [ ] Asynchronous: Event-driven messaging
  - [ ] API Gateway（Spring Cloud Gateway）
  - [ ] Service Discovery（Consul/Eureka）

- [ ] **Resilience4j パターン実装**
  - [ ] **Circuit Breaker**: 外部API呼び出し保護
  - [ ] **Retry**: 一時的障害への対応
  - [ ] **Rate Limiter**: API呼び出し頻度制御
  - [ ] **Time Limiter**: タイムアウト制御
  - [ ] **Bulkhead**: リソース分離
  - [ ] **Cache**: レスポンス時間改善

### Phase 6: アーキテクチャ拡張
- [ ] **イベント駆動アーキテクチャ**
  - [ ] Domain Events実装
  - [ ] Event Sourcing（検討）
  - [ ] CQRS パターン適用
  - [ ] Saga Pattern（分散トランザクション）

- [ ] **パフォーマンス最適化**
  - [ ] Read/Write DB分離
  - [ ] キャッシュ戦略（Redis）
  - [ ] バッチ処理基盤
  - [ ] Database per Service パターン

### Phase 5: セキュリティ・運用
- [ ] **認証・認可**
  - [ ] JWT-based認証
  - [ ] RBAC（Role-Based Access Control）
  - [ ] OAuth2/OIDC統合

- [ ] **セキュリティ強化**
  - [ ] API Rate Limiting
  - [ ] Input Validation強化
  - [ ] セキュリティスキャン自動化
