#!/usr/bin/env bash
# WSL 上で実行し、.cursor/mcp.json の petclinic URL を現在の WSL IP に合わせる。
# 使い方: bash scripts/update-mcp-url.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
MCP_JSON="$ROOT/.cursor/mcp.json"
IP="$(hostname -I | awk '{print $1}')"

if [[ -z "${IP}" ]]; then
  echo "WSL IP を取得できませんでした。" >&2
  exit 1
fi

mkdir -p "$(dirname "$MCP_JSON")"
cat >"$MCP_JSON" <<EOF
{
  "mcpServers": {
    "petclinic": {
      "url": "http://${IP}:8090/mcp"
    }
  }
}
EOF

echo "updated: $MCP_JSON"
echo "url:     http://${IP}:8090/mcp"
echo
echo "次を確認してください:"
echo "  1) WSL:     curl -s -o /dev/null -w '%{http_code}\\n' http://127.0.0.1:8090/mcp"
echo "  2) Windows: curl.exe -s -o /dev/null -w '%{http_code}\\n' http://${IP}:8090/mcp"
echo "  3) Cursor Settings → MCP → petclinic を Reload"
echo
echo "Windows の 2) が 000 のままなら、%UserProfile%\\.wslconfig に次を新規作成して wsl --shutdown してください:"
echo "  [wsl2]"
echo "  networkingMode=mirrored"
echo "その後 mcp.json を http://127.0.0.1:8090/mcp に戻し、Reload します。"
