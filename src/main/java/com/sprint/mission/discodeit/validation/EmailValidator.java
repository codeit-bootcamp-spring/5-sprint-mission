package com.sprint.mission.discodeit.validation;

import java.util.regex.Pattern;

public class EmailValidator {
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

  public static void validate(String email) {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("이메일은 필수입니다.");
    }
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
    }
  }
}
