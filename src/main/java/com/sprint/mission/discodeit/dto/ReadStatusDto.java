package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record ReadStatusDto() {

    public record CreateReadStatus(UUID userId, UUID channelId) {}
}