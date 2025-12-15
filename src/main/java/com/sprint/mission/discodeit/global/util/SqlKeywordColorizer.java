package com.sprint.mission.discodeit.global.util;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SqlKeywordColorizer {

    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";

    private static final Set<String> SQL_KEYWORDS = Set.of(
        "SELECT", "FROM", "WHERE", "JOIN", "INNER", "LEFT", "RIGHT", "OUTER", "FULL",
        "ON", "GROUP", "BY", "ORDER", "HAVING", "LIMIT", "OFFSET",
        "INSERT", "UPDATE", "DELETE", "MERGE",
        "VALUES", "SET",
        "CREATE", "DROP", "ALTER", "TRUNCATE",
        "TABLE", "INDEX", "VIEW", "SEQUENCE",
        "AND", "OR", "NOT", "IN", "LIKE", "BETWEEN", "IS", "NULL",
        "CASE", "WHEN", "THEN", "ELSE", "END",
        "AS", "WITH", "UNION", "EXCEPT", "INTERSECT",
        "DISTINCT", "ALL", "EXISTS",
        "ASC", "DESC"
    );

    private static final Pattern KEYWORD_PATTERN = Pattern.compile(
        "\\b(" + String.join("|", SQL_KEYWORDS) + ")\\b",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern STRING_PATTERN = Pattern.compile("'[^']*'");

    private SqlKeywordColorizer() {
    }

    public static String colorize(String sql) {
        if (sql == null || sql.isEmpty()) {
            return sql;
        }

        StringBuilder result = new StringBuilder();
        int lastIndex = 0;

        Matcher stringMatcher = STRING_PATTERN.matcher(sql);
        while (stringMatcher.find()) {
            String beforeString = sql.substring(lastIndex, stringMatcher.start());
            result.append(highlightKeywords(beforeString));
            result.append(CYAN).append(stringMatcher.group()).append(RESET);
            lastIndex = stringMatcher.end();
        }

        if (lastIndex < sql.length()) {
            result.append(highlightKeywords(sql.substring(lastIndex)));
        }

        return result.toString();
    }

    private static String highlightKeywords(String text) {
        return KEYWORD_PATTERN.matcher(text)
            .replaceAll(match -> BLUE + match.group(1).toUpperCase() + RESET);
    }
}
