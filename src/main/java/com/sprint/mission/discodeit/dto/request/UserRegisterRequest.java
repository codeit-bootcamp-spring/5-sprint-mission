package com.sprint.mission.discodeit.dto.request;

import java.time.LocalDate;

public record UserRegisterRequest(
        String email,
        String username,
        String password,
        LocalDate birthDate,
        boolean subscribedToNewsletter,
        String globalName
) {
}
