package org.springframework.samples.petclinic.mcp.client;

/**
 * ツールから投げる実行時例外。MCP クライアント（Cursor）にはエラーツール結果として伝わり、再試行の手がかりとなる。
 */
public class PetclinicApiException extends RuntimeException {

    public PetclinicApiException(String message) {
        super(message);
    }
}