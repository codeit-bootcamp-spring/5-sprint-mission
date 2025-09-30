package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

  // Create 요청용
  public Message toEntityForCreate(MessageDto dto, User author, Channel channel) {
    return new Message(dto.getContent(), author, channel);
  }

  // Entity → DTO 응답용
  public MessageDto toDto(Message message) {
    MessageDto dto = new MessageDto();
    dto.setId(message.getId());
    dto.setContent(message.getContent());
    dto.setAuthorId(message.getAuthor() != null ? message.getAuthor().getId() : null);
    dto.setChannelId(message.getChannel() != null ? message.getChannel().getId() : null);
    return dto;
  }

  // DTO 업데이트 내용만 Entity에 반영
  public void updateEntityFromDto(Message message, MessageDto dto) {
    if (dto.getNewContent() != null) {
      message.updateContent(dto.getNewContent());
    }
  }
}