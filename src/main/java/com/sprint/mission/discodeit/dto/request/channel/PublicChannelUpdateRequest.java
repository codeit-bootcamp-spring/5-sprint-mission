package com.sprint.mission.discodeit.dto.request.channel;

public record PublicChannelUpdateRequest(
        String newName,
        String newDescription
) {
}
