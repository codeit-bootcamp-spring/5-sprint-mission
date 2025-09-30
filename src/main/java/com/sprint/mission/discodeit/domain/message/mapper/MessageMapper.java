package com.sprint.mission.discodeit.domain.message.mapper;

import com.sprint.mission.discodeit.domain.message.dto.MessageDto;
import com.sprint.mission.discodeit.domain.user.mapper.UserMapper;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.domain.binarycontent.mapper.BinaryContentMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserMapper.class})
public interface MessageMapper {

  @Mapping(target = "channelId", source = "channel.id")
  MessageDto toDto(Message message);
}
