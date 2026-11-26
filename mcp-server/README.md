# Spring Petclinic MCP Server

Petclinic REST API を MCP（Streamable HTTP）で公開する独立サーバーです。設計の詳細は [MCP連携機能設計書](../docs/MCP導入/MCP連携機能設計書.md) を参照してください。

## ローカル起動（バックエンドが `localhost:9966` で動いている場合）

```bash
cd mcp-server
./mvnw spring-boot:run
```

MCP エンドポイント: `http://localhost:8090/mcp`

## Docker Compose（開発）

リポジトリルートで:

```bash
docker compose -f docker-compose.dev.yml up --build mcp-server
```

## 動作確認

### Cursor から接続して手動テストする

前提: `backend` と `mcp-server` が起動していること（Dev Container の **Reopen in Container**、または `docker compose -f docker-compose.dev.yml up --build`）。

1. **MCP サーバーが待ち受けているか確認する**

   ターミナルが **Dev Container 内**（プロンプトが `root@…:/workspace#` など）か **ホスト**（WSL / PowerShell で clone を開いている側）かでコマンドが異なります。`backend` コンテナには **Docker CLI は入っていない** ため、コンテナ内で `docker compose` を実行すると `command not found` になります。これは異常ではありません。

   **Dev Container 内で確認する場合**（API と MCP の疎通のみ）:

   ```bash
   curl -s -o /dev/null -w "api %{http_code}\n" http://localhost:9966/petclinic/api/pettypes
   curl -s -o /dev/null -w "mcp %{http_code}\n" http://mcp-server:8090/mcp
   ```

   API が `200` で、MCP が `400` または `405` など（エンドポイントは生きているが JSON-RPC でない素の GET/POST のため）なら、Compose 上の `mcp-server` は起動している可能性が高いです。確実に見るには **ホスト** で `docker compose ps` を実行してください。

   **ホスト（WSL など、リポジトリがある側）で確認する場合**:

   ```bash
   docker compose -f docker-compose.dev.yml ps mcp-server
   curl -s -o /dev/null -w "%{http_code}\n" http://localhost:9966/petclinic/api/pettypes
   curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8090/mcp
   ```

   `mcp-server` が `running`、curl が応答すれば次へ進みます。

2. **プロジェクトの MCP 設定を確認する**

   リポジトリ直下の [`.cursor/mcp.json`](../.cursor/mcp.json) で URL を指定します。Cursor の MCP クライアントは通常 **Windows 側** で動きます。WSL 上の Docker に対して Windows の `localhost:8090` が `ECONNREFUSED` になる場合は、WSL の IP を使います（`hostname -I | awk '{print $1}'`。再起動で変わることがあります）。

   ```json
   {
     "mcpServers": {
       "petclinic": {
         "url": "http://192.168.40.58:8090/mcp"
       }
     }
   }
   ```

   WSL から `curl http://127.0.0.1:8090/mcp` が通るのに Cursor だけ失敗するときは、上記の IP 指定を疑ってください。

3. **Cursor で設定を読み込む**

   - リポジトリを Cursor で開く（フォルダはホスト上の clone で問題ありません）。
   - **Cursor Settings → MCP**（または **Features → MCP**）を開き、`petclinic` が一覧に出ているか確認します。
   - 出ていない、またはエラー表示のときはウィンドウを **Reload Window** し、Dev Container 起動後にもう一度 MCP 一覧を開きます。

4. **接続状態を確認する**

   - MCP 一覧で `petclinic` が **接続済み**（緑／Connected）になっていること。
   - ツール数が **9**、リソースが **1**（`petclinic://openapi.yml`）程度であること。

5. **ツールを手動で試す**

   ツール名（`list_pet_types` など）は自分で入力しません。Cursor の **Agent チャット**に、次の文をそのまま貼ります。`petclinic` が接続済みなら、エージェントが対応するツールを選んで実行します。

   種別一覧（`list_pet_types`、引数なし）。`cat` や `dog` など 6 件返れば成功です。

   ```text
   petclinic の MCP ツール list_pet_types を実行して、返ってきたペット種別の名前を全部列挙して。
   ```

   飼い主検索（`list_owners`、`lastName` は `鈴木`）。鈴木 花子 と 鈴木 次郎 が出れば成功です。

   ```text
   petclinic の MCP ツール list_owners を、lastName に 鈴木 を渡して実行して。見つかった飼い主の姓名を列挙して。
   ```

   詳細取得（`get_owner`、`ownerId` は `1`）。山田 太郎 とペット レオ が出れば成功です。

   ```text
   petclinic の MCP ツール get_owner を、ownerId に 1 を渡して実行して。飼い主の名前と、飼っているペットの名前を教えて。
   ```

   獣医一覧（`list_vets`、引数なし）。田中 健 などが出れば成功です。

   ```text
   petclinic の MCP ツール list_vets を実行して、獣医の名前を列挙して。
   ```

   存在しない ID（`get_owner`、`ownerId` は `999`）。「見つかりません」という旨の返答になれば成功です。

   ```text
   petclinic の MCP ツール get_owner を、ownerId に 999 を渡して実行して。結果をそのまま教えて。
   ```

6. **画面・API と突き合わせる**

   - ブラウザで http://localhost:4444 を開き、Find Owners で `鈴木`、Veterinarians 一覧など、上記結果と一致するか見ます。
   - 必要なら `curl http://localhost:9966/petclinic/api/owners?lastName=%E9%88%B4%E6%9C%A8` と比較します。

登録系（`create_owner` など）はデータを増やすため、手動テストは任意です。実行する場合は実行前確認を有効にし、確認後に画面を再読み込みしてください。

**接続できないとき**

| 症状 | 確認すること |
| --- | --- |
| Connection refused（`ECONNREFUSED 127.0.0.1:8090`） | WSL では届くが Windows / Cursor から `127.0.0.1` も WSL IP も `000` のときは、WSL の localhost 転送が無効。`.cursor/mcp.json` を WSL IP（`hostname -I`）の `http://<IP>:8090/mcp` にするか、`%UserProfile%\.wslconfig` に `networkingMode=mirrored` を新規作成して `wsl --shutdown` |
| 個人情報ツールが同意メッセージだけ返す | 想定どおり。ユーザー同意後に `grant_personal_data_consent`（`confirmed=true`）して再実行 |
| ツールは動くが結果が空／エラー | `backend` が起動しているか、`PETCLINIC_API_BASE_URL` が `http://backend:9966/petclinic/api` になっているか（Compose 内） |
| 一覧にサーバーが出ない | `.cursor/mcp.json` の場所（リポジトリルート）、Reload Window、Docker / Dev Container の再起動 |

### MCP Inspector（任意）

ホストまたは `frontend` コンテナ:

```bash
npx @modelcontextprotocol/inspector
```

接続 URL: `http://localhost:8090/mcp`

## テスト時のやり取りの可視化

Cursor の **リクエスト／レスポンス（JSON-RPC）**、`[mcp-trace]` ログ、ソースファイルの対応は **[docs/CursorとMCPのやり取り.md](docs/CursorとMCPのやり取り.md)** にまとめています。

ログを追いながら試すとき（ホスト側）:

```bash
docker compose -f docker-compose.dev.yml logs -f mcp-server 2>&1 | grep '\[mcp-trace\]'
```

## ログの確認

開発環境では、各コンテナの標準出力にログが出ます。**`docker compose logs` はホスト側**（WSL / PowerShell など、Dev Container の外のターミナル）で実行してください。Dev Container 内の `backend` シェルには Docker CLI がないため、`docker` コマンドは使えません。

### Docker Compose（開発）で起動している場合

リポジトリ直下で:

```bash
# MCP サーバーだけ（追従表示）
docker compose -f docker-compose.dev.yml logs -f mcp-server

# 直近 100 行だけ
docker compose -f docker-compose.dev.yml logs --tail=100 mcp-server

# backend / frontend とまとめて
docker compose -f docker-compose.dev.yml logs -f backend frontend mcp-server
```

| サービス | ログに含まれる内容の例 |
| --- | --- |
| `mcp-server` | `[mcp-trace]`（入出力・ツール・API 呼び出し）、エラー時の DEBUG 詳細 |
| `backend` | Maven 再コンパイル、`spring-boot:run`、REST API のアクセスログ |
| `frontend` | webpack のビルド・再コンパイル |

コンテナ名は `docker compose -f docker-compose.dev.yml ps` で確認できます（プロジェクト名はフォルダ名に依存し、`spring-petclinic-mcp-server-1` のようになります）。

### Cursor / VS Code から見る

- **ホストに「Docker」拡張機能**がある場合: コンテナ一覧 → `spring-petclinic-mcp-server-1`（名前は環境により異なる）→ **Logs**。
- **Dev Containers で開いている場合**: ターミナルプロファイルで **Local**（WSL ホスト）を選び、上記 `docker compose logs` を実行する。

### MCP のログレベルを上げる

既定で `application.yml` は `org.springframework.samples.petclinic.mcp.client` を `DEBUG` にしています。さらに詳しく見る場合は、`docker-compose.dev.yml` の `mcp-server` に例えば次を追加してコンテナを再起動します。

```yaml
environment:
  LOGGING_LEVEL_ROOT: INFO
  LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SAMPLES_PETCLINIC_MCP: DEBUG
```

### ホストで `./mvnw spring-boot:run` した場合

`mcp-server` ディレクトリで起動したターミナルに、そのままコンソールログが出ます。ファイルには書き出していません。

### Cursor の MCP クライアント側

ツール呼び出しの成否は **Cursor Settings → MCP** のサーバー状態や、Agent チャットの応答で確認します。MCP サーバー側の処理内容は、上記 **`mcp-server` コンテナのログ** を見てください。

## 個人情報の同意ゲート

飼い主・ペット・獣医・来院など **個人情報を含むツール結果** は、ユーザー同意なしでは返しません。

1. Agent が該当ツールを呼ぶと、同意要求メッセージだけが返り、実データは含まれません。
2. ユーザーに同意を確認する。
3. 同意後、`grant_personal_data_consent` を `confirmed=true` で実行する。
4. 同じ照会ツールをもう一度実行する。

関連ツール: `grant_personal_data_consent` / `revoke_personal_data_consent` / `get_personal_data_consent_status`。同意は MCP セッション（`Mcp-Session-Id`）単位でプロセス内に保持します。無効化は `MCP_PERSONAL_DATA_CONSENT_REQUIRED=false`。

## 主な環境変数

| 変数 | 既定値 |
| --- | --- |
| `PETCLINIC_API_BASE_URL` | `http://localhost:9966/petclinic/api` |
| `MCP_SERVER_PORT` | `8090` |
| `MCP_ALLOWED_ORIGINS` | `http://localhost,http://127.0.0.1` |
| `MCP_TRACE_ENABLED` | `true`（`[mcp-trace]` ログを出す） |
| `MCP_PERSONAL_DATA_CONSENT_REQUIRED` | `true`（個人情報レスポンスに同意を要求） |