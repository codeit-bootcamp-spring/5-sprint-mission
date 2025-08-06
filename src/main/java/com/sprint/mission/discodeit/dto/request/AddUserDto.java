package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record AddUserDto(String userName, String email, String password, String phoneNumber, UUID profileId) {
}
