package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ChannelMapper {

  @Autowired
  protected ReadStatusRepository readStatusRepository;
  @Autowired
  protected MessageRepository messageRepository;
  @Autowired
  protected UserMapper userMapper;

  @Mapping(target = "participants", expression = "java(toParticipants(channel))")
  @Mapping(target = "lastMessageAt", expression = "java(toLastMessageAt(channel))")
  public abstract ChannelDto toDto(Channel channel);

  protected List<UserDto> toParticipants(Channel channel) {
    return readStatusRepository.findAllByChannelId(channel.getId())
        .stream()
        .map(ReadStatus::getUser)
        .map(userMapper::toDto)
        .toList();
  }

  protected Instant toLastMessageAt(Channel channel) {
    return messageRepository.findAllByChannelId(channel.getId())
        .stream()
        .map(Message::getCreatedAt)
        .max(Comparator.naturalOrder())
        .orElse(Instant.MIN);
  }
}
