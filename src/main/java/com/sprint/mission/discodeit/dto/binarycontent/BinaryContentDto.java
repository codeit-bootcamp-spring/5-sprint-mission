package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import java.util.UUID;

public record BinaryContentDto(

    UUID id,
    String filename,
    long size,
    String contentType
) {

  public static BinaryContentDto from(BinaryContent bc) {
    return new BinaryContentDto(
        bc.getId(),
        bc.getFilename(),
        bc.getSize(),
        bc.getContentType()
    );
  }
}
