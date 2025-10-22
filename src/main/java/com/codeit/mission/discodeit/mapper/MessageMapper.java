package com.codeit.mission.discodeit.mapper;

import com.codeit.mission.discodeit.dto.data.MessageDto;
import com.codeit.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class,
        UserMapper.class})
public interface MessageMapper {

    @Mapping(target = "channelId", source = "channel.id")
    MessageDto toDto(Message message);
}
