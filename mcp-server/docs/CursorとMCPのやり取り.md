# Cursor と mcp-server のやり取り（可視化ガイド）

手動テスト時に **Cursor（MCP クライアント）** と **`mcp-server`** の間でどのような HTTP / JSON-RPC が行われるか、サーバー側ログとの対応をまとめたドキュメントです。

関連: [README.md](../README.md)（起動・接続手順）、[MCP連携機能設計書](../../docs/MCP導入/MCP連携機能設計書.md)

## 1. プロトコルの前提

| 項目 | 本リポジトリでの値 |
| --- | --- |
| トランスポート | **Streamable HTTP**（`spring.ai.mcp.server.protocol=STREAMABLE`） |
| エンドポイント | `http://localhost:8090/mcp`（Compose 内は `http://mcp-server:8090/mcp`） |
| メッセージ形式 | **JSON-RPC 2.0**（MCP 仕様に沿った `method` / `params` / `result`） |
| 本文のログ | **出さない**（`McpHttpTraceFilter` は method・path・status のみ。設計書 9 章） |

Cursor はホスト上で動き、`.cursor/mcp.json` の URL へ接続します。Dev Container 内のエディタから見ても、多くの場合 MCP クライアントはホスト側です。

## 2. 接続時のやり取り（ツール実行の前）

初回接続時、Cursor はおおよそ次の JSON-RPC を `POST /mcp` で送ります（**実際の JSON は Cursor / MCP SDK のバージョンで細部が異なる場合があります**）。

### 2.1 `initialize`（クライアント → サーバー）

クライアントがプロトコル版と能力を伝え、サーバー情報を得ます。

```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "initialize",
  "params": {
    "protocolVersion": "2024-11-05",
    "capabilities": {},
    "clientInfo": {
      "name": "cursor",
      "version": "…"
    }
  }
}
```

### 2.2 `initialize` のレスポンス（サーバー → クライアント）

`mcp-server` は `application.yml` の `spring.ai.mcp.server.name` などを含めて応答します。

```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "result": {
    "protocolVersion": "2024-11-05",
    "capabilities": {
      "tools": { "listChanged": false },
      "resources": { "listChanged": false }
    },
    "serverInfo": {
      "name": "petclinic-mcp-server",
      "version": "0.1.0"
    }
  }
}
```

その後、クライアントは `notifications/initialized` を送ることがあります（応答なしの通知）。

### 2.3 `tools/list`（ツール一覧）

```json
{
  "jsonrpc": "2.0",
  "id": 2,
  "method": "tools/list",
  "params": {}
}
```

レスポンスには `list_owners` など **9 個のツール**定義（`name`、`description`、入力スキーマ）が含まれます。実装は `tools/OwnerTools.java` などの `@McpTool` から Spring AI が生成します。

```json
{
  "jsonrpc": "2.0",
  "id": 2,
  "result": {
    "tools": [
      {
        "name": "list_owners",
        "description": "飼い主を検索します。lastName を指定すると…",
        "inputSchema": { "type": "object", "properties": { "lastName": { "type": "string" } } }
      }
    ]
  }
}
```

（`tools` 配列は省略せず 9 件並びます。ここでは 1 件のみ記載。）

### 2.4 この段階のサーバーログ

接続だけのときは `[mcp-trace] inbound` のみ出ることが多く、`tool` / `outbound-api` はまだ出ません。処理本体は **Spring AI MCP**（`spring-ai-starter-mcp-server-webmvc`）の自動構成が担当し、リポジトリ内の自前クラスは [`McpHttpTraceFilter.java`](../src/main/java/org/springframework/samples/petclinic/mcp/config/McpHttpTraceFilter.java) による境界ログが中心です。

## 3. ツール 1 回分の詳細（`list_owners` / `lastName=Davis`）

Agent チャットで飼い主検索を依頼すると、Cursor が **`tools/call`** を送ります。

### 3.1 Cursor からのリクエスト（代表例）

HTTP:

```http
POST /mcp HTTP/1.1
Host: localhost:8090
Content-Type: application/json
Accept: application/json, text/event-stream
```

本文（JSON-RPC）:

```json
{
  "jsonrpc": "2.0",
  "id": 42,
  "method": "tools/call",
  "params": {
    "name": "list_owners",
    "arguments": {
      "lastName": "Davis"
    }
  }
}
```

| フィールド | 意味 |
| --- | --- |
| `method` | 常に `tools/call`（ツール実行） |
| `params.name` | MCP ツール名（`OwnerTools` の `@McpTool(name = "list_owners")`） |
| `params.arguments` | ツール引数（Java の `listOwners(String lastName)` にバインド） |

### 3.2 mcp-server 内部の処理順

```mermaid
sequenceDiagram
    participant C as Cursor
    participant M as mcp-server<br/>Spring AI MCP
    participant T as OwnerTools
    participant API as PetclinicApiClient
    participant B as backend REST

    C->>M: POST /mcp tools/call
    M->>T: listOwners("Davis")
    T->>API: listOwners("Davis")
    API->>B: GET /petclinic/api/owners?lastName=Davis
    B-->>API: 200 JSON 配列
    API-->>T: List Owner
    T-->>M: List Owner
    M-->>C: CallToolResult
```

### 3.3 既存 API の応答（backend → mcp-server）

`PetclinicApiClient#listOwners` が受け取る JSON の例（抜粋）:

```json
[
  {
    "id": 2,
    "firstName": "Betty",
    "lastName": "Davis",
    "address": "638 Cardinal Ave.",
    "city": "Sun Prairie",
    "telephone": "6085551749",
    "pets": []
  },
  {
    "id": 4,
    "firstName": "Harold",
    "lastName": "Davis",
    "address": "563 Friendly St.",
    "city": "Windsor",
    "telephone": "6085553198",
    "pets": []
  }
]
```

処理は既存の [`OwnerRestController`](../../src/main/java/org/springframework/samples/petclinic/rest/controller/OwnerRestController.java)（`mcp-server` 外）が担当します。

### 3.4 mcp-server から Cursor へのレスポンス（代表例）

Spring AI / MCP SDK がツール戻り値を **CallToolResult** に包みます。構造は次のとおりです（本文は Jackson により JSON 文字列化されることが多い）。

```json
{
  "jsonrpc": "2.0",
  "id": 42,
  "result": {
    "content": [
      {
        "type": "text",
        "text": "[{\"id\":2,\"firstName\":\"Betty\",\"lastName\":\"Davis\",...},{\"id\":4,\"firstName\":\"Harold\",\"lastName\":\"Davis\",...}]"
      }
    ],
    "isError": false
  }
}
```

| フィールド | 意味 |
| --- | --- |
| `result.content` | MCP がモデル／UI に渡すペイロード（本サーバーは主に `type: text`） |
| `result.isError` | `false` なら成功。ツール内で `PetclinicApiException` などが起きた場合は `true` になり、メッセージが content に入る |

Cursor の **Agent チャット**には、この `text` を要約した自然文として表示されます。生 JSON を見るにはサーバーログの `resultPreview` か、下記の API を直接叩く方が確実です。

### 3.5 エラー例（`get_owner` / `ownerId=999`）

リクエスト:

```json
{
  "jsonrpc": "2.0",
  "id": 43,
  "method": "tools/call",
  "params": {
    "name": "get_owner",
    "arguments": { "ownerId": 999 }
  }
}
```

backend が 404 のとき、ツールは `PetclinicApiException` を投げ、クライアント向けには概ね次の形になります（文言は `ApiErrorTranslator` のメッセージ）。

```json
{
  "jsonrpc": "2.0",
  "id": 43,
  "result": {
    "content": [
      {
        "type": "text",
        "text": "飼い主 ID 999 は見つかりません。list_owners で ID を確認してください。"
      }
    ],
    "isError": true
  }
}
```

## 4. `[mcp-trace]` ログとソースの対応

既定で `MCP_TRACE_ENABLED=true`（`petclinic.mcp.trace-enabled`）。ホストで:

```bash
docker compose -f docker-compose.dev.yml logs -f mcp-server 2>&1 | grep '\[mcp-trace\]'
```

### 4.1 フェーズとソース

| ログの `phase` / 種別 | 意味 | 主なソース（`mcp-server/`） |
| --- | --- | --- |
| `inbound phase=start/end` | Cursor → `/mcp` の HTTP | [`config/McpHttpTraceFilter.java`](../src/main/java/org/springframework/samples/petclinic/mcp/config/McpHttpTraceFilter.java)（手前で [`OriginValidationFilter.java`](../src/main/java/org/springframework/samples/petclinic/mcp/config/OriginValidationFilter.java)） |
| `tool phase=start/success/error` | ツール名・引数・結果プレビュー | [`McpToolTraceAspect.java`](../src/main/java/org/springframework/samples/petclinic/mcp/config/McpToolTraceAspect.java) + 各 `tools/*.java` |
| `outbound-api phase=start/end` | 既存 API への HTTP | [`PetclinicApiTraceInterceptor.java`](../src/main/java/org/springframework/samples/petclinic/mcp/config/PetclinicApiTraceInterceptor.java) + [`PetclinicApiClient.java`](../src/main/java/org/springframework/samples/petclinic/mcp/client/PetclinicApiClient.java) |

トレースの on/off: [`McpTraceProperties.java`](../src/main/java/org/springframework/samples/petclinic/mcp/config/McpTraceProperties.java)。

### 4.2 `list_owners` 1 回分のログ行

```text
[mcp-trace] inbound phase=start method=POST path=/mcp origin=-
[mcp-trace] tool phase=start name=list_owners args=lastName=Davis
[mcp-trace] outbound-api phase=start method=GET uri=http://backend:9966/petclinic/api/owners?lastName=Davis
[mcp-trace] outbound-api phase=end ... status=200 durationMs=...
[mcp-trace] tool phase=success name=list_owners durationMs=... resultPreview=[{...}]
[mcp-trace] inbound phase=end method=POST path=/mcp status=200 durationMs=...
```

| # | ログ行（抜粋） | 処理 | ソースファイル |
| --- | --- | --- | --- |
| 1 | `inbound phase=start` | `tools/call` の HTTP 受信開始 | `McpHttpTraceFilter.java`（JSON-RPC 解析は Spring AI MCP） |
| 2 | `tool phase=start name=list_owners` | ツール実行開始 | `McpToolTraceAspect.java` → `OwnerTools.java` |
| 3–4 | `outbound-api` … `GET …/owners?lastName=Davis` | 既存 API 呼び出し | `PetclinicApiTraceInterceptor.java` → `PetclinicApiClient#listOwners` |
| 5 | `tool phase=success` … `resultPreview=` | Java 戻り値を MCP 結果へ | `OwnerTools.java` → Spring AI が CallToolResult 化 |
| 6 | `inbound phase=end` … `status=200` | HTTP 応答完了 | `McpHttpTraceFilter.java` |

### 4.3 ログと JSON-RPC の対応表

| 観測できるもの | Cursor リクエスト／レスポンス | `[mcp-trace]` | 備考 |
| --- | --- | --- | --- |
| HTTP 到達 | `POST /mcp` | `inbound start/end` | JSON 本文はログに無し |
| どのツールか | `params.name` | `tool name=` | 一致する |
| 引数 | `params.arguments` | `tool args=` | 一致する |
| Petclinic API | （クライアントは見えない） | `outbound-api uri=` | Cursor からは不可視 |
| ツール結果の中身 | `result.content[].text` | `resultPreview=` | プレビューは最大 500 文字 |
| エラー | `isError: true` | `tool phase=error` | メッセージは `ApiErrorTranslator` 由来 |

## 5. Cursor 側で確認できること

| 場所 | 内容 |
| --- | --- |
| **Settings → MCP** | `petclinic` 接続状態、登録ツール数（9） |
| **Agent チャット** | 選ばれたツール名と、結果の要約文 |
| **Output**（MCP / Cursor MCP） | 接続エラーなど（バージョンで名称が異なる） |

エンドツーエンドで **リクエスト／レスポンス JSON の全文**を見るには、MCP Inspector（`npx @modelcontextprotocol/inspector`）で `tools/call` を直接送る方法もあります。

## 6. backend ログとの同時監視

```bash
docker compose -f docker-compose.dev.yml logs -f mcp-server backend 2>&1 | grep -E '\[mcp-trace\]|GET /petclinic/api'
```

`outbound-api` の URI と、backend のアクセスログのパスが同じタイミングで並べば、Cursor → MCP → 既存 API まで一連の流れを追えます。