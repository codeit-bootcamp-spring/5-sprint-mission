package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {

  public BinaryContentDto.Detail toDetail(BinaryContent binaryContent) {
    if (binaryContent == null) {
      return null;
    }

    return BinaryContentDto.Detail.builder()
                                  .id(binaryContent.getId())
                                  .contentType(binaryContent.getContentType())
                                  .fileName(binaryContent.getFileName())
                                  .size(binaryContent.getSize())
                                  .build();
  }

  public BinaryContentDto.Detail toDetail(BinaryContent binaryContent, byte[] bytes) {
    if (binaryContent == null) {
      return null;
    }

    return BinaryContentDto.Detail.builder()
                                  .id(binaryContent.getId())
                                  .bytes(bytes)
                                  .contentType(binaryContent.getContentType())
                                  .fileName(binaryContent.getFileName())
                                  .size(binaryContent.getSize())
                                  .build();
  }
}
