package com.sprint.mission.discodeit.domain.binarycontent.dto;

public record BinaryContentCreateRequest(
    String fileName,
    String contentType,
    byte[] bytes
) {

}
