package com.sprint.mission.discodeit.dto.request.user;

public record UserUpdateProfileSettingsRequest(

        String globalName,
        String bio
) {
}
