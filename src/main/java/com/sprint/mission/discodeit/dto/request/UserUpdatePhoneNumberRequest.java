package com.sprint.mission.discodeit.dto.request;

import java.util.Optional;

public record UserUpdatePhoneNumberRequest(
        Optional<String> phoneNumber
) {
}
