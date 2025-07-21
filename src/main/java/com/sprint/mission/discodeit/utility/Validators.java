package com.sprint.mission.discodeit.utility;

import com.sprint.mission.discodeit.entity.User;
import java.util.regex.Pattern;

public class Validators {
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
  private static final Pattern PASSWORD_PATTERN =
      Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+]{8,}$");

  private Validators() {}

  public static String validateEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("⚠ 이메일은 필수 항목입니다.");
    }

    String normalizedEmail = StringUtil.normalizeString(email).toLowerCase();

    if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
      throw new IllegalArgumentException("⚠ 이메일 형식이 올바르지 않습니다. 예: example@domain.com");
    }

    return normalizedEmail;
  }

  public static String validatePassword(String password) {
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("⚠ 비밀번호는 필수 항목입니다.");
    }

    String normalizedPassword = StringUtil.normalizeString(password);

    if (!PASSWORD_PATTERN.matcher(normalizedPassword).matches()) {
      throw new IllegalArgumentException("⚠ 비밀번호는 영문, 숫자, 특수문자를 포함하여 8자 이상이어야 합니다.");
    }

    return normalizedPassword;
  }

  public static User validateUser(User user) {
    if (user == null) {
      throw new IllegalArgumentException("⚠ 유저 객체는 null일 수 없습니다.");
    }

    Validators.validateEmail(user.getEmail());

    if (user.getUsername() == null || user.getUsername().isBlank()) {
      throw new IllegalArgumentException("⚠ 사용자명은 필수입니다.");
    }

    if (user.getBirthDate() == null) {
      throw new IllegalArgumentException("⚠ 생년월일은 필수입니다.");
    }

    return user;
  }
}
