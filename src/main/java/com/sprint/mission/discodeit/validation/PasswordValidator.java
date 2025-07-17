package com.sprint.mission.discodeit.validation;

import java.util.regex.Pattern;

public class PasswordValidator {
  private static final Pattern PASSWORD_PATTERN =
      Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+]{8,}$");

  public static void validate(String password) {
    if (password == null) {
      throw new IllegalArgumentException("비밀번호는 필수입니다.");
    }
    if (!PASSWORD_PATTERN.matcher(password).matches()) {
      throw new IllegalArgumentException("비밀번호는 영문, 숫자, 특수문자 포함 8자 이상이어야 합니다.");
    }
  }
}
