package org.springframework.samples.petclinic.mcp.privacy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

/**
 * セッション単位の原文→仮名の対応表。同じ原文は同じ仮名に揃え、関係性を保つ（conceal 相当）。
 */
@Component
public class PseudonymDictionary {

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> bySession = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    public String map(String category, String original) {
        if (original == null || original.isBlank()) {
            return original;
        }
        String session = McpSessionContext.get();
        ConcurrentHashMap<String, String> map = bySession.computeIfAbsent(session, key -> new ConcurrentHashMap<>());
        String key = category + ":" + original;
        return map.computeIfAbsent(key, ignored -> nextPseudonym(session, category, original));
    }

    public void clearCurrentSession() {
        String session = McpSessionContext.get();
        bySession.remove(session);
        counters.remove(session);
    }

    public Map<String, String> snapshotCurrentSession() {
        ConcurrentHashMap<String, String> map = bySession.get(McpSessionContext.get());
        return map == null ? Map.of() : Map.copyOf(map);
    }

    private String nextPseudonym(String session, String category, String original) {
        int index = counters.computeIfAbsent(session, key -> new AtomicInteger()).incrementAndGet();
        return switch (category) {
            case "lastName" -> LAST_NAMES[(index - 1) % LAST_NAMES.length];
            case "firstName" -> FIRST_NAMES[(index - 1) % FIRST_NAMES.length];
            case "city" -> CITIES[(index - 1) % CITIES.length];
            case "petName" -> PET_NAMES[(index - 1) % PET_NAMES.length];
            case "visitDescription" -> "来院記録" + index;
            default -> "MASK-" + category + "-" + index;
        };
    }

    private static final String[] LAST_NAMES = {
        "仮山", "仮川", "仮田", "仮村", "仮野", "仮原", "仮橋", "仮木", "仮本", "仮井"
    };

    private static final String[] FIRST_NAMES = {
        "一郎", "二郎", "三郎", "花子", "美子", "太郎", "次郎", "春子", "秋子", "冬子"
    };

    private static final String[] CITIES = {
        "仮市", "仮区", "仮町", "仮村", "仮府"
    };

    private static final String[] PET_NAMES = {
        "ポチ", "タマ", "ミケ", "シロ", "クロ", "チビ", "マル", "ソラ", "モモ", "コテツ"
    };
}
