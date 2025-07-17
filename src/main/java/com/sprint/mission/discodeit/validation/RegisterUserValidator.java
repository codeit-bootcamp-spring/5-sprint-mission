package com.sprint.mission.discodeit.validation;

import com.sprint.mission.discodeit.entity.User;
import java.util.regex.Pattern;

public class RegisterUserValidator {
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
  private static final Pattern PASSWORD_PATTERN =
      Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+]{8,}$");

  public static void validate(User user) {
    if (user == null) {
      throw new IllegalArgumentException("유저 객체가 null입니다.");
    }
    if (user.getEmail() == null || user.getEmail().isBlank()) {
      throw new IllegalArgumentException("이메일은 필수입니다.");
    }
    if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
      throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
    }
    if (user.getUsername() == null || user.getUsername().isBlank()) {
      throw new IllegalArgumentException("사용자명은 필수입니다.");
    }
    if (user.getPassword() == null || !PASSWORD_PATTERN.matcher(user.getPassword()).matches()) {
      throw new IllegalArgumentException("비밀번호는 영문, 숫자, 특수문자 포함 8자 이상이어야 합니다.");
    }
    if (user.getBirthDate() == null) {
      throw new IllegalArgumentException("생년월일은 필수입니다.");
    }
  }
}
