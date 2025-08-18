package com.sprint.mission.discodeit.dto.response.binarycontent;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import java.util.UUID;

public record BinaryContentResponse(
    UUID id,
    String filename,
    String contentType,
    long size,
    byte[] bytes
) {

  public static BinaryContentResponse from(BinaryContent bc) {
    return new BinaryContentResponse(
        bc.getId(),
        bc.getFilename(),
        bc.getContentType(),
        bc.getSize(),
        bc.getBytes()
    );
  }

}
