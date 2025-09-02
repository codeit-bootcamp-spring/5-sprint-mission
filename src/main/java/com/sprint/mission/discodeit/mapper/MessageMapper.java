package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {

  private final UserMapper userMapper;
  private final ChannelMapper channelMapper;
  private final BinaryContentMapper binaryContentMapper;

  public MessageDto.Detail toDetail(Message message) {
    if (message == null) {
      return null;
    }

    return MessageDto.Detail.builder()
                            .id(message.getId())
                            .author(userMapper.toDetail(message.getAuthor()))
                            .channel(channelMapper.toDetail(message.getChannel()))
                            .content(message.getContent())
                            .attachments(message.getAttachments()
                                                .stream()
                                                .map(binaryContentMapper::toDetail)
                                                .toList())
                            .createdAt(message.getCreatedAt())
                            .updatedAt(message.getUpdatedAt())
                            .build();
  }
}
