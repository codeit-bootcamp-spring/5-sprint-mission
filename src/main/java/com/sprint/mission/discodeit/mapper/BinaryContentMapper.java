package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BinaryContentMapper {
  public BinaryContentResponse toDto(BinaryContent content) {
    if (content == null) return null;

    return new BinaryContentResponse(
        content.getId(),
        content.getFileName(),
        content.getSize(),
        content.getContentType()
    );
  }
}
