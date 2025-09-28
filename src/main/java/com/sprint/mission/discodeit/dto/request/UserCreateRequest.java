package com.sprint.mission.discodeit.dto.request;

<<<<<<< HEAD
public record UserCreateRequest(
        String username,
        String email,
        String password
) {
=======
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(

    @NotBlank(message = "Username must not be blank") @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters") String username,

    @Email(message = "Email should be valid") @NotBlank(message = "Email is mandatory") String email,


    @NotBlank(message = "Password is mandatory") @Size(min = 6, max = 20, message = "Password must be at least 6 characters") String password) {

>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
}
