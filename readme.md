# Spring Petclinic (React)

Spring Boot（バックエンド API）と React / TypeScript（フロントエンド）で構成された Petclinic アプリケーションの環境です。

本リポジトリは [spring-petclinic-reactjs](https://github.com/spring-petclinic/spring-petclinic-reactjs) および元となる [spring-petclinic](https://github.com/spring-projects/spring-petclinic) をベースに構成しています。

## アクセス先

- **フロントエンド（画面）**: `http://localhost:4444`
- **バックエンド（API）**: `http://localhost:9966/petclinic/`

## 起動方法（Docker Compose）

ホスト側に JDK・Maven・Node.js をインストールせずに起動できます。

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

ソースコードを編集しながら開発する場合は、開発用 Compose ファイルまたは Dev Containers を利用できます。

```bash
docker compose -f docker-compose.dev.yml up --build
```

VS Code / Cursor を利用する場合は、リポジトリを開いてコマンドパレットから **Dev Containers: Reopen in Container** を実行します。

停止・コンテナ削除:

```bash
docker compose -f docker-compose.dev.yml down
```

## データベースについて

デフォルトでは API コンテナ内のインメモリデータベース（HSQLDB）を使用します。コンテナを停止するとデータは初期化されます。

## 参考リンク

- [Spring Petclinic](https://github.com/spring-projects/spring-petclinic)
- [Spring Petclinic ReactJS](https://github.com/spring-petclinic/spring-petclinic-reactjs)
- [Spring Petclinic GraphQL](https://github.com/spring-petclinic/spring-petclinic-graphql)