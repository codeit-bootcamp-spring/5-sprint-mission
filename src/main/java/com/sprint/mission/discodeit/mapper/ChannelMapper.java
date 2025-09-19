package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring" ,uses = {UserMapper.class})
public interface ChannelMapper {
//  MessageRepository messageRepository();
//  ReadStatusRepository readStatusRepository();

  ChannelDto toDto(Channel channel);

}
