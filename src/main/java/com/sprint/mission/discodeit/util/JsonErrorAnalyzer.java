package com.sprint.mission.discodeit.util;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JsonErrorAnalyzer {

    private static final Pattern FIELD_PATTERN = Pattern.compile("\\[\"([^\"]+)\"]");
    private static final Pattern TYPE_PATTERN = Pattern.compile("type `([^`]+)`");

    private JsonErrorAnalyzer() {
        throw new AssertionError("Utility class");
    }

    public static Map<String, Object> analyze(Throwable cause, boolean exposeDetails) {
        Map<String, Object> details = new HashMap<>();

        if (cause instanceof JsonParseException parseEx) {
            analyzeJsonParseException(parseEx, details, exposeDetails);
        } else if (cause instanceof InvalidFormatException formatEx) {
            analyzeInvalidFormatException(formatEx, details, exposeDetails);
        } else if (cause instanceof UnrecognizedPropertyException unrecognizedEx) {
            analyzeUnrecognizedPropertyException(unrecognizedEx, details, exposeDetails);
        } else if (cause instanceof MismatchedInputException mismatchEx) {
            analyzeMismatchedInputException(mismatchEx, details, exposeDetails);
        } else if (cause instanceof JsonMappingException mappingEx) {
            analyzeJsonMappingException(mappingEx, details, exposeDetails);
        } else {
            details.put("errorType", "INVALID_REQUEST_BODY");
        }

        return details;
    }

    private static void analyzeJsonParseException(
        JsonParseException ex,
        Map<String, Object> details,
        boolean exposeDetails
    ) {
        details.put("errorType", "SYNTAX_ERROR");
        if (exposeDetails) {
            details.put("hint", "JSON 구문이 올바르지 않습니다. 중괄호, 대괄호, 따옴표를 확인해주세요.");
            JsonLocation location = ex.getLocation();
            if (location != null) {
                details.put("location", String.format("line: %d, column: %d",
                    location.getLineNr(), location.getColumnNr()));
            }
        }
    }

    private static void analyzeInvalidFormatException(
        InvalidFormatException ex,
        Map<String, Object> details,
        boolean exposeDetails
    ) {
        details.put("errorType", "FORMAT_ERROR");
        if (exposeDetails) {
            String fieldName = extractFieldName(ex);
            String targetType = extractTargetType(ex);

            if (fieldName != null) {
                details.put("field", fieldName);
            }
            if (targetType != null) {
                String simplified = simplifyTypeName(targetType);
                details.put("expectedType", simplified);
                details.put("hint", getHintForType(simplified));
            }
        }
    }

    private static void analyzeUnrecognizedPropertyException(
        UnrecognizedPropertyException ex,
        Map<String, Object> details,
        boolean exposeDetails
    ) {
        details.put("errorType", "UNKNOWN_FIELD");
        if (exposeDetails) {
            details.put("field", ex.getPropertyName());
            details.put("hint", "알 수 없는 필드입니다. API 문서를 확인해주세요.");
        }
    }

    private static void analyzeMismatchedInputException(
        MismatchedInputException ex,
        Map<String, Object> details,
        boolean exposeDetails
    ) {
        details.put("errorType", "TYPE_MISMATCH");
        if (exposeDetails) {
            String fieldName = extractFieldName(ex);
            String targetType = extractTargetType(ex);

            if (fieldName != null) {
                details.put("field", fieldName);
            }
            if (targetType != null) {
                String simplified = simplifyTypeName(targetType);
                details.put("expectedType", simplified);
                details.put("hint", getHintForType(simplified));
            }
        }
    }

    private static void analyzeJsonMappingException(
        JsonMappingException ex,
        Map<String, Object> details,
        boolean exposeDetails
    ) {
        details.put("errorType", "MAPPING_ERROR");
        if (exposeDetails) {
            String fieldName = extractFieldName(ex);
            if (fieldName != null) {
                details.put("field", fieldName);
            }
            details.put("hint", "JSON 구조가 예상과 다릅니다.");
        }
    }

    private static String extractFieldName(JsonMappingException ex) {
        if (ex.getPath() != null && !ex.getPath().isEmpty()) {
            JsonMappingException.Reference lastRef = ex.getPath().get(ex.getPath().size() - 1);
            return lastRef.getFieldName();
        }

        String message = ex.getMessage();
        if (message != null) {
            Matcher matcher = FIELD_PATTERN.matcher(message);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return null;
    }

    private static String extractTargetType(JsonMappingException ex) {
        String message = ex.getMessage();
        if (message == null) {
            return null;
        }

        Matcher matcher = TYPE_PATTERN.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    private static String simplifyTypeName(String fullTypeName) {
        if (fullTypeName == null) {
            return null;
        }

        String simplified = fullTypeName.replaceAll("<.*?>", "");

        int lastDot = simplified.lastIndexOf('.');
        if (lastDot > 0) {
            return simplified.substring(lastDot + 1);
        }

        return simplified;
    }

    private static String getHintForType(String type) {
        if (type == null) {
            return "올바른 형식으로 입력해주세요.";
        }

        return switch (type) {
            case "Long", "Integer", "Short", "Byte" -> "정수 형식으로 입력해주세요.";
            case "Double", "Float", "BigDecimal" -> "숫자 형식으로 입력해주세요.";
            case "Boolean" -> "true 또는 false로 입력해주세요.";
            case "LocalDateTime", "LocalDate", "Instant" -> "날짜/시간 형식을 확인해주세요. (예: 2024-11-22T10:30:45)";
            case "UUID" -> "UUID 형식으로 입력해주세요.";
            case "ArrayList", "List", "Set" -> "배열 형식으로 입력해주세요. (예: [\"item1\", \"item2\"])";
            default -> "올바른 형식으로 입력해주세요.";
        };
    }
}
