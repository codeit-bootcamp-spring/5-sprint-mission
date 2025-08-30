package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;

@Mapper
@AllArgsConstructor
public class MessageMapper {

  private final BinaryContentMapper binaryContentMapper;
  private final UserMapper userMapper;

  public MessageDto toDto(Message message) {
    return MessageDto.builder()
        .id(message.getId())
        .createdAt(message.getCreatedAt())
        .updatedAt(message.getUpdatedAt())
        .content(message.getContent())
        .channelId(message.getChannel().getId())
        .author(userMapper.toDto(message.getAuthor()))
        .attachments(message.getAttachments()
            .stream()
            .map(binaryContentMapper::toDto)
            .toList())
        .build();
  }
}
