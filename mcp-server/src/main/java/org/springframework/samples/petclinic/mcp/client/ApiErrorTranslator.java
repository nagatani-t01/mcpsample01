package org.springframework.samples.petclinic.mcp.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

/**
 * {@link org.springframework.web.client.RestClientResponseException} や接続失敗を AI 向けメッセージに変換する。
 * 入力エラーは {@code errors} ヘッダー、DB 制約違反は応答本文のクラス名のみをユーザーに返し、SQL 詳細は DEBUG ログのみ。
 */
@Component
public class ApiErrorTranslator {

    private static final Logger log = LoggerFactory.getLogger(ApiErrorTranslator.class);

    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public ApiErrorTranslator(ObjectMapper objectMapper, org.springframework.samples.petclinic.mcp.config.PetclinicApiProperties properties) {
        this.objectMapper = objectMapper;
        this.baseUrl = properties.getBaseUrl();
    }

    public PetclinicApiException translateResponse(RestClientResponseException ex, String notFoundHint) {
        HttpStatusCode status = ex.getStatusCode();
        int code = status.value();
        if (code == 404) {
            if (notFoundHint != null && !notFoundHint.isBlank()) {
                return new PetclinicApiException(notFoundHint);
            }
            return new PetclinicApiException("要求したデータは見つかりません。");
        }
        if (code == 401 || code == 403) {
            return new PetclinicApiException(
                "Petclinic API の認証に失敗しました。PETCLINIC_API_USERNAME / PETCLINIC_API_PASSWORD を確認してください。");
        }
        if (code == 400) {
            String errorsHeader = ex.getResponseHeaders() != null ? ex.getResponseHeaders().getFirst("errors") : null;
            if (errorsHeader != null && !errorsHeader.isBlank()) {
                return new PetclinicApiException(formatValidationErrors(errorsHeader));
            }
            return new PetclinicApiException(formatGenericBadRequest(ex.getResponseBodyAsString()));
        }
        if (code >= 500) {
            return new PetclinicApiException(
                "Petclinic API でエラーが発生しました（HTTP " + code + "）。backend コンテナのログを確認してください。");
        }
        return new PetclinicApiException("Petclinic API が要求を拒否しました（HTTP " + code + "）。");
    }

    public PetclinicApiException translateConnection(Throwable ex) {
        if (ex instanceof ResourceAccessException && ex.getCause() instanceof java.net.http.HttpTimeoutException) {
            return new PetclinicApiException("Petclinic API が 10 秒以内に応答しませんでした。時間をおいて再試行してください。");
        }
        return new PetclinicApiException(
            "Petclinic API（" + baseUrl + "）に接続できません。backend コンテナが起動しているか確認してください。");
    }

    private String formatValidationErrors(String errorsHeader) {
        try {
            List<Map<String, Object>> errors = objectMapper.readValue(errorsHeader, new TypeReference<>() {});
            List<String> lines = new ArrayList<>();
            lines.add("入力内容に誤りがあります。");
            for (Map<String, Object> item : errors) {
                String field = String.valueOf(item.getOrDefault("fieldName", "field"));
                String value = String.valueOf(item.getOrDefault("fieldValue", ""));
                String message = String.valueOf(item.getOrDefault("errorMessage", ""));
                lines.add(field + ": " + message + "（入力値: " + value + "）");
            }
            return String.join("\n", lines);
        }
        catch (Exception parseError) {
            log.debug("Failed to parse errors header: {}", errorsHeader, parseError);
            return "入力内容に誤りがあります。各項目の形式と必須入力を確認してください。";
        }
    }

    private String formatGenericBadRequest(String body) {
        if (body == null || body.isBlank()) {
            return "Petclinic API が要求を処理できませんでした。入力データと関連 ID を確認してください。";
        }
        try {
            Map<String, Object> map = objectMapper.readValue(body, new TypeReference<>() {});
            String className = String.valueOf(map.getOrDefault("className", "Exception"));
            String exMessage = String.valueOf(map.getOrDefault("exMessage", ""));
            log.debug("Petclinic API bad request: {} — {}", className, exMessage);
            String simpleName = className.contains(".") ? className.substring(className.lastIndexOf('.') + 1) : className;
            return "Petclinic API が要求を処理できませんでした（" + simpleName
                + "）。関連するデータが存在するか確認してください。";
        }
        catch (Exception parseError) {
            log.debug("Failed to parse error body: {}", body, parseError);
            return "Petclinic API が要求を処理できませんでした。関連するデータが存在するか確認してください。";
        }
    }
}