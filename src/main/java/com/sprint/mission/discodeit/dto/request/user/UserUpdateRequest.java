package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

    @Size(min = 2, max = 32)
    @Pattern(regexp = "^(?!.*\\.\\.)[A-Za-z0-9._]+$")
    String newUsername,

    @Size(min = 6, max = 254)
    @Email
    String newEmail,

    @Size(min = 8, max = 72)
    String newPassword
) {

  @AssertTrue(message = "newUsername, newEmail, newPassword 중 하나는 필수입니다")
  public boolean hasAny() {
    return newUsername != null || newEmail != null || newPassword != null;
  }
}
