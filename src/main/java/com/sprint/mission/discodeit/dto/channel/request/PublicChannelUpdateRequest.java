package com.sprint.mission.discodeit.dto.channel.request;

public record PublicChannelUpdateRequest(
        String newName,
        String newDescription
) {}
