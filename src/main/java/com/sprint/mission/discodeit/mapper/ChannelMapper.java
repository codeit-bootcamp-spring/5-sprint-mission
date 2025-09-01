package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.Comparator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ChannelMapper {

  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserMapper userMapper;

  public ChannelDto toDto(Channel channel) {

    return ChannelDto.builder()
        .id(channel.getId())
        .type(channel.getType())
        .name(channel.getName())
        .description(channel.getDescription())
        .participants(readStatusRepository.findByChannelId(channel.getId())
            .stream()
            .map(readStatus -> userMapper.toDto(readStatus.getUser()))
            .toList())
        .lastMessageAt(messageRepository.findAllByChannelId(channel.getId())
            .stream()
            .max(Comparator.comparingLong(m -> m.getCreatedAt().getEpochSecond()))
            .map(Message::getCreatedAt)
            .orElse(null))
        .build();
  }

}
