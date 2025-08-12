package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.exception.ValidatorsValidationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import static com.sprint.mission.discodeit.util.StringUtil.extractDigits;
import static com.sprint.mission.discodeit.util.StringUtil.normalizeString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Validators {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+]{8,}$");
    private static final int MAX_BIO_LENGTH = 190;
    private static final int MIN_GUILD_NAME_LENGTH = 2;
    private static final int MAX_GUILD_NAME_LENGTH = 100;
    private static final int MIN_CHANNEL_NAME_LENGTH = 1;
    private static final int MAX_CHANNEL_NAME_LENGTH = 100;
    private static final int MAX_MESSAGE_CONTENT_LENGTh = 2000;

    private static String normalizeAndCheckBlank(String input, String errorMessage) {
        String normalized = normalizeString(input);
        if (normalized.isBlank()) throw new ValidatorsValidationException(errorMessage);
        return normalized;
    }

    public static String validateEmail(String email) {
        String normalizedEmail = normalizeAndCheckBlank(email, "⚠ 이메일은 필수 항목입니다.").toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches())
            throw new ValidatorsValidationException("⚠ 이메일 형식이 올바르지 않습니다.");
        return normalizedEmail;
    }

    public static String validatePassword(String password) {
        String normalizedPassword = normalizeAndCheckBlank(password, "⚠ 비밀번호는 필수 항목입니다.");
        if (!PASSWORD_PATTERN.matcher(normalizedPassword).matches())
            throw new ValidatorsValidationException("⚠ 비밀번호는 영문, 숫자, 특수문자를 포함하여 8자 이상이어야 합니다.");
        return normalizedPassword;
    }

    public static String validateGlobalName(String globalName) {
        String normalizedGlobalName = normalizeString(globalName);
        if (normalizedGlobalName.isBlank()) return null;
        if (normalizedGlobalName.length() > 20) throw new ValidatorsValidationException("별명은 20자 이내여야 합니다.");
        return normalizedGlobalName;
    }

    public static String validateUsername(String username) {
        String normalizedUsername = normalizeAndCheckBlank(username, "⚠ 사용자명은 필수 항목입니다.").toLowerCase();
        if (normalizedUsername.length() < 2 || normalizedUsername.length() > 20)
            throw new ValidatorsValidationException("⚠ 사용자명은 2~20자 이내여야 합니다.");
        return normalizedUsername;
    }

    public static String validatePhoneNumber(String phoneNumber) {
        String digits = extractDigits(phoneNumber);
        if (digits.isBlank()) return null;
        if (!digits.matches("\\d{10,13}")) throw new ValidatorsValidationException("전화번호는 10~13자리 숫자만 가능합니다.");
        return digits;
    }

    public static String validateBio(String bio) {
        String normalizedBio = normalizeString(bio);
        if (normalizedBio.isBlank()) return null;
        if (normalizedBio.length() > MAX_BIO_LENGTH)
            throw new ValidatorsValidationException("내 소개는 " + MAX_BIO_LENGTH + "자 이하여야 합니다.");
        return normalizedBio;
    }

    public static String validateGuildName(String guildName) {
        String normalizedGuildName = normalizeString(guildName);
        if (normalizedGuildName.length() < MIN_GUILD_NAME_LENGTH || normalizedGuildName.length() > MAX_GUILD_NAME_LENGTH)
            throw new ValidatorsValidationException(MIN_GUILD_NAME_LENGTH + "자에서 " + MAX_GUILD_NAME_LENGTH + "자 사이여야 해요.");
        return normalizedGuildName;
    }

    public static String validateUri(String uriStr) {
        String normalizedUriStr = normalizeString(uriStr);
        if (normalizedUriStr.isBlank()) throw new ValidatorsValidationException("올바른 URL이어야 합니다.");
        try {
            URI uri = new URI(normalizedUriStr);
            String scheme = uri.getScheme();
            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                throw new ValidatorsValidationException("URL은 http 또는 https만 허용합니다.");
            }
            return normalizedUriStr;
        } catch (URISyntaxException e) {
            throw new ValidatorsValidationException("올바른 URL이어야 합니다.");
        }
    }

    public static String validateChannelName(String channelName) {
        String normalizedChannelName = normalizeString(channelName);
        if (normalizedChannelName.isEmpty() || normalizedChannelName.length() > MAX_CHANNEL_NAME_LENGTH)
            throw new ValidatorsValidationException(MIN_CHANNEL_NAME_LENGTH + "자에서 " + MAX_CHANNEL_NAME_LENGTH + "자 사이여야 해요.");
        return normalizedChannelName;
    }

    public static String validateMessageContent(String content) {
        if (content == null) return null;
        content = content.strip();
        if (content.isEmpty()) throw new ValidatorsValidationException("내용은 비워둘 수 없습니다.");

        int cpLen = content.codePointCount(0, content.length());
        if (cpLen > MAX_MESSAGE_CONTENT_LENGTh) {
            throw new ValidatorsValidationException("메시지는 최대 " + MAX_MESSAGE_CONTENT_LENGTh + "자까지 가능합니다. 현재: " + cpLen + "자");
        }
        return content;
    }
}
