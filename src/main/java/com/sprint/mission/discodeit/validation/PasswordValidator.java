package com.sprint.mission.discodeit.validation;

import java.util.regex.Pattern;

public final class PasswordValidator {
  private static final Pattern PASSWORD_PATTERN =
      Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+]{8,}$");

  private PasswordValidator() {}

  public static void validate(String password) {
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("⚠ 비밀번호는 필수 항목입니다.");
    }
    if (!PASSWORD_PATTERN.matcher(password).matches()) {
      throw new IllegalArgumentException("⚠ 비밀번호는 영문, 숫자, 특수문자를 포함하여 8자 이상이어야 합니다.");
    }
  }
}
