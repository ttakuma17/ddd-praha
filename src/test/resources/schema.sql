-- テスト用のデータベーススキーマ

-- メンバーテーブル
CREATE TABLE IF NOT EXISTS members (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL
);