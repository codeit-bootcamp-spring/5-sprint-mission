package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UserRegisterRequest(

    @NotBlank
    @Size(min = 6, max = 254)
    @Email
    String email,

    @Size(max = 32)
    String globalName,

    @NotBlank
    @Size(min = 2, max = 32)
    @Pattern(
        regexp = "^(?!.*\\.\\.)[a-z0-9._]+$",
        flags = Pattern.Flag.CASE_INSENSITIVE
    )
    String username,

    @NotBlank
    @Size(min = 8, max = 72)
    String password,

    @NotNull
    @Past
    LocalDate birthDate,

    boolean subscribedToNewsletter
) {

}
