package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {

  private final BinaryContentMapper binaryContentMapper;
  private final UserMapper userMapper;

  public MessageDto toDto(Message m) {
    List<BinaryContentDto> files = m.getAttachments().stream()
        .map(binaryContentMapper::toDto).toList();
    return new MessageDto(
        m.getId(), m.getCreatedAt(), m.getUpdatedAt(),
        m.getContent(), m.getChannel().getId(),
        userMapper.toDto(m.getAuthor()), files
    );
  }
}