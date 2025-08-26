package com.sprint.mission.discodeit.dto.response.binarycontent;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import java.time.Instant;
import java.util.UUID;

public record BinaryContentResponse(

    UUID id,
    Instant createdAt,
    String filename,
    long size,
    String contentType,
    byte[] bytes
) {

  public static BinaryContentResponse from(BinaryContent bc) {
    return new BinaryContentResponse(
        bc.getId(),
        bc.getCreatedAt(),
        bc.getFilename(),
        bc.getSize(),
        bc.getContentType(),
        bc.getBytes()
    );
  }
}
