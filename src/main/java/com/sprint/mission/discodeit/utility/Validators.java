package com.sprint.mission.discodeit.utility;

import static com.sprint.mission.discodeit.utility.StringUtil.extractDigits;
import static com.sprint.mission.discodeit.utility.StringUtil.normalizeString;

import com.sprint.mission.discodeit.exception.ValidationException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

public class Validators {
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
  private static final Pattern PASSWORD_PATTERN =
      Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+]{8,}$");

  private Validators() {}

  public static String validateEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new ValidationException("⚠ 이메일은 필수 항목입니다.");
    }
    String normalizedEmail = normalizeString(email).toLowerCase();
    if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
      throw new ValidationException("⚠ 이메일 형식이 올바르지 않습니다.");
    }
    return normalizedEmail;
  }

  public static String validatePassword(String password) {
    String normalizedPassword = normalizeString(password);
    if (normalizedPassword.isBlank()) {
      throw new ValidationException("⚠ 비밀번호는 필수 항목입니다.");
    }
    if (!PASSWORD_PATTERN.matcher(normalizedPassword).matches()) {
      throw new ValidationException("⚠ 비밀번호는 영문, 숫자, 특수문자를 포함하여 8자 이상이어야 합니다.");
    }
    return normalizedPassword;
  }

  public static String validateGlobalName(String globalName) {
    String normalizedGlobalName = normalizeString(globalName);
    if (normalizedGlobalName.length() > 20) {
      throw new ValidationException("별명은 20자 이내여야 합니다.");
    }
    return normalizedGlobalName;
  }

  public static String validateUsername(String username) {
    String normalizedUsername = normalizeString(username);
    if (normalizedUsername.isBlank()) {
      throw new ValidationException("⚠ 사용자명은 필수입니다.");
    }
    if (normalizedUsername.length() < 2 || normalizedUsername.length() > 20) {
      throw new ValidationException("⚠ 사용자명은 2~20자 이내여야 합니다.");
    }
    return normalizedUsername;
  }

  public static String validatePhoneNumber(String phoneNumber) {
    String digits = extractDigits(phoneNumber);
    if (digits.isBlank()) {
      return "";
    }

    if (!digits.matches("\\d{10,13}")) {
      throw new ValidationException("전화번호는 10~13자리 숫자만 가능합니다.");
    }

    return digits;
  }

  public static String validateBio(String bio) {
    String normalizedBio = normalizeString(bio);
    if (normalizedBio.length() > 100) {
      throw new ValidationException("자기소개는 100자 이하여야 합니다.");
    }
    return normalizedBio;
  }

  public static String validateGuildName(String guildName) {
    String normalizedGuildName = normalizeString(guildName);

    if (normalizedGuildName.length() > 20) {
      throw new ValidationException("서버명은 20자 이내여야 합니다.");
    }
    return normalizedGuildName;
  }

  public static String validateUri(String uriStr) {
    String normalizedUriStr = normalizeString(uriStr);
    try {
      URI uri = new URI(normalizedUriStr);
      String scheme = uri.getScheme();
      if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
        throw new ValidationException("URL은 http 또는 https만 허용합니다.");
      }
      return normalizedUriStr;
    } catch (URISyntaxException e) {
      throw new ValidationException("올바른 URL이어야 합니다.");
    }
  }
}
