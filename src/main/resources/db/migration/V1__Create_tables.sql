-- V1__Create_tables.sql
-- 初期テーブル作成

-- メンバーテーブル
CREATE TABLE IF NOT EXISTS members (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL
);

-- チームテーブル
CREATE TABLE IF NOT EXISTS teams (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- タスクテーブル
CREATE TABLE IF NOT EXISTS tasks (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- チームメンバーテーブル（中間テーブル）
CREATE TABLE IF NOT EXISTS team_members (
    team_id VARCHAR(255) NOT NULL,
    member_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (team_id, member_id),
    FOREIGN KEY (team_id) REFERENCES teams(id),
    FOREIGN KEY (member_id) REFERENCES members(id)
);

-- メンバータスクテーブル（メンバーとタスクの進捗管理）
CREATE TABLE IF NOT EXISTS member_tasks (
    member_id VARCHAR(255) NOT NULL,
    task_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    PRIMARY KEY (member_id, task_id),
    FOREIGN KEY (member_id) REFERENCES members(id),
    FOREIGN KEY (task_id) REFERENCES tasks(id)
);