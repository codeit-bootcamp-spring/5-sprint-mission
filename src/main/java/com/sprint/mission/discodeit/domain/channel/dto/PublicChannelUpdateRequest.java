package com.sprint.mission.discodeit.domain.channel.dto;

public record PublicChannelUpdateRequest(
    String newName,
    String newDescription
) {

}
