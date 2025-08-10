package com.sprint.mission.discodeit.dto.request;

public record ProfileImageCommand(
        String filename,
        String contentType,
        byte[] data
) {
}
