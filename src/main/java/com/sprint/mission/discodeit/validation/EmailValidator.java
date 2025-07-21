package com.sprint.mission.discodeit.validation;

import java.util.regex.Pattern;

public final class EmailValidator {
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

  private EmailValidator() {}

  public static void isValid(String email) {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("⚠ 이메일은 필수 항목입니다.");
    }

    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new IllegalArgumentException("⚠ 이메일 형식이 올바르지 않습니다. 예: example@domain.com");
    }
  }
}
