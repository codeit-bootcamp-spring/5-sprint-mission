package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.exception.ValidatorsValidationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.sprint.mission.discodeit.util.StringUtil.extractDigits;
import static com.sprint.mission.discodeit.util.StringUtil.normalizeString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Validators {

    private static final int MAX_BIO_LENGTH = 190;

    private static final int MIN_GUILD_NAME_LENGTH = 2;
    private static final int MAX_GUILD_NAME_LENGTH = 100;

    private static final int MIN_CHANNEL_NAME_LENGTH = 1;
    private static final int MAX_CHANNEL_NAME_LENGTH = 100;

    private static final int MIN_USERNAME_LENGTH = 2;
    private static final int MAX_USERNAME_LENGTH = 20;

    private static final int MIN_PASSWORD_LENGTH = 8;

    private static final int MIN_PHONE_DIGITS = 10;
    private static final int MAX_PHONE_DIGITS = 13;

    private static final int MAX_MESSAGE_CONTENT_LENGTH = 2000;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    private static final String SPECIALS = "!@#$%^&*()_+-";
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile(
                    "^(?=.*[A-Za-z])(?=.*\\d)(?=.*["
                            + Pattern.quote(SPECIALS)
                            + "])[A-Za-z\\d"
                            + Pattern.quote(SPECIALS)
                            + "]{"
                            + MIN_PASSWORD_LENGTH
                            + ",}$");

    public static String validateEmail(String email) {
        String normalized = requireNormalizedNotBlank(email, "이메일은 필수 항목입니다.").toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new ValidatorsValidationException("이메일 형식이 올바르지 않습니다.");
        }
        return normalized;
    }

    public static String validatePassword(String password) {
        String normalized = requireNormalizedNotBlank(password, "비밀번호는 필수 항목입니다.");
        if (!PASSWORD_PATTERN.matcher(normalized).matches()) {
            throw new ValidatorsValidationException(
                    "비밀번호는 영문, 숫자, 특수문자(" + SPECIALS + ")를 각각 1자 이상 포함하고 " + MIN_PASSWORD_LENGTH + "자 이상이어야 합니다.");
        }
        return normalized;
    }

    public static String validateGlobalName(String globalName) {
        String normalized = normalizeSafe(globalName);
        if (normalized.isBlank()) return null;
        requireLengthBetween(normalized, 1, 20, "별명은 1~20자 이내여야 합니다.");
        return normalized;
    }

    public static String validateUsername(String username) {
        String normalized = requireNormalizedNotBlank(username, "사용자명은 필수 항목입니다.").toLowerCase();
        requireLengthBetween(
                normalized,
                MIN_USERNAME_LENGTH,
                MAX_USERNAME_LENGTH,
                "사용자명은 " + MIN_USERNAME_LENGTH + "~" + MAX_USERNAME_LENGTH + "자 이내여야 합니다.");
        return normalized;
    }

    public static String validatePhoneNumber(String phoneNumber) {
        String digits = extractDigits(phoneNumber);
        if (digits.isBlank()) return null;
        if (!digits.matches("\\d{" + MIN_PHONE_DIGITS + "," + MAX_PHONE_DIGITS + "}")) {
            throw new ValidatorsValidationException("전화번호는 " + MIN_PHONE_DIGITS + "~" + MAX_PHONE_DIGITS + "자리 숫자만 가능합니다.");
        }
        return digits;
    }

    public static String validateBio(String bio) {
        String normalized = normalizeSafe(bio);
        if (normalized.isBlank()) return null;
        if (normalized.length() > MAX_BIO_LENGTH) {
            throw new ValidatorsValidationException("내 소개는 " + MAX_BIO_LENGTH + "자 이하여야 합니다.");
        }
        return normalized;
    }

    public static String validateGuildName(String guildName) {
        String normalized = normalizeSafe(guildName);
        requireLengthBetween(normalized, MIN_GUILD_NAME_LENGTH, MAX_GUILD_NAME_LENGTH,
                MIN_GUILD_NAME_LENGTH + "자에서 " + MAX_GUILD_NAME_LENGTH + "자 사이여야 해요.");
        return normalized;
    }

    public static String validateChannelName(String channelName) {
        String normalized = normalizeSafe(channelName);
        requireLengthBetween(normalized, MIN_CHANNEL_NAME_LENGTH, MAX_CHANNEL_NAME_LENGTH,
                MIN_CHANNEL_NAME_LENGTH + "자에서 " + MAX_CHANNEL_NAME_LENGTH + "자 사이여야 해요.");
        return normalized;
    }

    public static String validateUri(String uriStr) {
        String normalized = requireNormalizedNotBlank(uriStr, "올바른 URL이어야 합니다.");
        try {
            URI uri = new URI(normalized);
            String scheme = uri.getScheme();
            String host = uri.getHost();

            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                throw new ValidatorsValidationException("URL은 http 또는 https만 허용합니다.");
            }
            if (host == null || host.isBlank()) {
                throw new ValidatorsValidationException("올바른 URL이어야 합니다.");
            }
            return normalized;
        } catch (URISyntaxException e) {
            throw new ValidatorsValidationException("올바른 URL이어야 합니다.");
        }
    }

    public static String validateMessageContent(String content) {
        if (content == null) return null;
        String normalized = content.strip();
        if (normalized.isEmpty()) {
            throw new ValidatorsValidationException("내용은 비워둘 수 없습니다.");
        }
        int cpLen = normalized.codePointCount(0, normalized.length());
        if (cpLen > MAX_MESSAGE_CONTENT_LENGTH) {
            throw new ValidatorsValidationException("메시지는 최대 " + MAX_MESSAGE_CONTENT_LENGTH + "자까지 가능합니다. 현재: " + cpLen + "자");
        }
        return normalized;
    }

    private static String normalizeSafe(String input) {
        return normalizeString(Objects.toString(input, ""));
    }

    private static String requireNormalizedNotBlank(String input, String errorMessage) {
        String normalized = normalizeSafe(input);
        if (normalized.isBlank()) {
            throw new ValidatorsValidationException(errorMessage);
        }
        return normalized;
    }

    private static void requireLengthBetween(String s, int min, int max, String messageIfInvalid) {
        if (s == null) throw new ValidatorsValidationException(messageIfInvalid);
        int len = s.length();
        if (len < min || len > max) throw new ValidatorsValidationException(messageIfInvalid);
    }
}
