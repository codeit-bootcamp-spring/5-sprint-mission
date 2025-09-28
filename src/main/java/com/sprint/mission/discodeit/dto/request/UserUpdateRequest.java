package com.sprint.mission.discodeit.dto.request;

<<<<<<< HEAD
public record UserUpdateRequest(
        String username,
        String email,
        String password
) {}
=======
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @NotNull(message = "ID is mandatory") @Size(min = 3, max = 20, message = "ID must be at least 1 character") String newUsername,
    @Email(message = "Email should be valid") @NotBlank(message = "Email is mandatory") String newEmail,
    @NotBlank(message = "Password is mandatory") @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters") String newPassword) {

}
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
