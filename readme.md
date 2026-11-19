# Spring Petclinic (React)

Spring Boot（バックエンド API）と React / TypeScript（フロントエンド）で構成された Petclinic アプリケーションの環境です。

本リポジトリは [spring-petclinic-reactjs](https://github.com/spring-petclinic/spring-petclinic-reactjs) および元となる [spring-petclinic](https://github.com/spring-projects/spring-petclinic) をベースに構成しています。

Petclinic の REST API を MCP（Model Context Protocol）経由で AI から操作できるようにする **`mcp-server`** も含みます（開発環境向け）。

## アクセス先

| 用途 | URL（開発時） |
| --- | --- |
| フロントエンド（画面） | http://localhost:4444 |
| バックエンド（API） | http://localhost:9966/petclinic/ |
| MCP サーバー（Cursor などから接続） | http://localhost:8090/mcp |

## 起動方法（Docker Compose・本番用イメージ）

ホスト側に JDK・Maven・Node.js をインストールせずに起動できます。**この構成には `mcp-server` は含まれません**（画面と API のみ）。

```bash
docker compose up --build
```

API の動作確認例:

```bash
curl http://localhost:9966/petclinic/api/pettypes
```

停止・コンテナ削除:

```bash
docker compose down
```

## 開発用環境（Docker Compose / Dev Containers）

ソースコードを編集しながら開発する場合は、開発用 Compose ファイルまたは Dev Containers を利用します。**MCP を使う場合も、必ずこちら（`docker-compose.dev.yml`）を使います。**

```bash
docker compose -f docker-compose.dev.yml up --build
```

VS Code / Cursor を利用する場合は、リポジトリを開いてコマンドパレットから **Dev Containers: Reopen in Container** を実行します。`backend`・`frontend`・**`mcp-server`** が同時に起動します。

停止・コンテナ削除:

```bash
docker compose -f docker-compose.dev.yml down
```

くわしい構築手順・トラブルシュートは [docs/環境構築手順.md](docs/環境構築手順.md) を参照してください。

## MCP を Cursor から使うまでの手順

開発環境が起動し、Cursor から MCP ツールを呼び出せる状態にするまでの流れです。

### 前提

- [Docker](https://docs.docker.com/) がホストで動いていること
- [Cursor](https://cursor.com/)（または VS Code + Dev Containers 拡張）
- Dev Containers 拡張機能（Cursor / VS Code で **Reopen in Container** する場合）

### 手順一覧

| # | やること | 完了の目安 |
| --- | --- | --- |
| 1 | 開発環境を起動する | **Dev Containers: Reopen in Container**、またはホストで `docker compose -f docker-compose.dev.yml up --build` |
| 2 | API が応答する | `curl http://localhost:9966/petclinic/api/pettypes` が JSON を返す（Dev Container 内のターミナルでも可） |
| 3 | MCP が応答する | ホストで `curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8090/mcp` がエラーではないこと（`400` などでもエンドポイントは生きている）。Dev Container 内なら `http://mcp-server:8090/mcp` で疎通確認 |
| 4 | MCP 設定を確認する | リポジトリ直下の [`.cursor/mcp.json`](.cursor/mcp.json) に `petclinic` → `http://localhost:8090/mcp` があること |
| 5 | Cursor でサーバーを読み込む | **Cursor Settings → MCP** に `petclinic` が表示される。出ない場合は **Reload Window** |
| 6 | 接続を確認する | MCP 一覧で `petclinic` が接続済み。ツール **9** 件・リソース **1** 件程度 |
| 7 | 動作を試す | Agent チャットで例: 「`petclinic` の MCP ツール `list_pet_types` を実行して、種別名を全部列挙して」 |
| 8 | 画面と照合する（任意） | http://localhost:4444 の表示と結果が一致すること |

**補足**

- Dev Container **内**のターミナルには `docker` コマンドはありません。コンテナ状態やログは **ホスト側**（WSL など）で `docker compose -f docker-compose.dev.yml ps` / `logs` を実行します。
- Cursor の MCP クライアントは **ホスト側** で動くことが多いため、接続 URL は **`http://localhost:8090/mcp`** です（`mcp-server` ホスト名はコンテナ間用）。

### 詳細ドキュメント

| 内容 | 参照先 |
| --- | --- |
| Cursor 向けテスト文・接続トラブル | [mcp-server/README.md](mcp-server/README.md) |
| ログの見方 | [mcp-server/README.md](mcp-server/README.md)（「ログの確認」）、[docs/環境構築手順.md](docs/環境構築手順.md) §3.3 |
| 設計・ツール一覧 | [docs/MCP導入/MCP連携機能設計書.md](docs/MCP導入/MCP連携機能設計書.md) |

## データベースについて

デフォルトでは API コンテナ内のインメモリデータベース（HSQLDB）を使用します。コンテナを停止するとデータは初期化されます。

## 参考リンク

- [Spring Petclinic](https://github.com/spring-projects/spring-petclinic)
- [Spring Petclinic ReactJS](https://github.com/spring-petclinic/spring-petclinic-reactjs)
- [Spring Petclinic GraphQL](https://github.com/spring-petclinic/spring-petclinic-graphql)