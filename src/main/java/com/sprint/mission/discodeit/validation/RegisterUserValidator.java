package com.sprint.mission.discodeit.validation;

import com.sprint.mission.discodeit.entity.User;

public class RegisterUserValidator {
  public static void validate(User user) {
    if (user == null) {
      throw new IllegalArgumentException("유저 객체가 null입니다.");
    }

    EmailValidator.validate(user.getEmail());

    if (user.getUsername() == null || user.getUsername().isBlank()) {
      throw new IllegalArgumentException("사용자명은 필수입니다.");
    }

    PasswordValidator.validate(user.getPassword());

    if (user.getBirthDate() == null) {
      throw new IllegalArgumentException("생년월일은 필수입니다.");
    }
  }
}
