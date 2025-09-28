package com.sprint.mission.discodeit.dto.request;

<<<<<<< HEAD
public record LoginRequest (
    String username,
    String password
) {
=======
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(@NotBlank(message = "Username is mandatory") String username,

                           @NotBlank(message = "Password is mandatory") @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters") String password) {
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)

}
