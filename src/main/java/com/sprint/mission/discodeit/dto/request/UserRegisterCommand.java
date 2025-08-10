package com.sprint.mission.discodeit.dto.request;

import java.time.LocalDate;

public record UserRegisterCommand(
        String email,
        String username,
        String password,
        LocalDate birthDate,
        boolean subscribedToNewsletter,
        String globalName,
        ProfileImageCommand profile
) {
}
