package com.sprint.mission.discodeit.dto.request;

public record ProfileImageCommand(
        byte[] bytes,
        String filename,
        String contentType
) {
}
