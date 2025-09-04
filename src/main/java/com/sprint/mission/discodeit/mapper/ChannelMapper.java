package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ChannelMapper {

  private MessageRepository messageRepository;
  private ReadStatusRepository readStatusRepository;
  private UserMapper userMapper;

  @Autowired
  void setDependencies(
      MessageRepository messageRepository,
      ReadStatusRepository readStatusRepository,
      UserMapper userMapper) {
    this.messageRepository = messageRepository;
    this.readStatusRepository = readStatusRepository;
    this.userMapper = userMapper;
  }

  @Mapping(target = "participants", ignore = true)
  @Mapping(target = "lastMessageAt", ignore = true)
  public abstract ChannelDto toDto(Channel channel);

  @AfterMapping
  protected void fillDerivedFields(Channel channel,
      @MappingTarget ChannelDto.ChannelDtoBuilder target) {

    target.participants(readStatusRepository.findAllByChannelId(channel.getId())
        .stream()
        .map(readStatus -> userMapper.toDto(readStatus.getUser()))
        .toList());

    target.lastMessageAt(
        messageRepository.findTopByChannelIdOrderByCreatedAtDescIdDesc(channel.getId())
            .map(Message::getCreatedAt)
            .orElse(null)
    );
  }
}
