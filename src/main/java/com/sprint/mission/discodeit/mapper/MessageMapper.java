package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserMapper.class})
public interface MessageMapper {
//    UUID id,
//    Instant createdAt,
//    Instant updatedAt,
//    String content,
//    UUID channelId,
//    UserDto author,
//    List<BinaryContentDto> attachments

    @Mapping(target = "channelId", source = "channel.id")
    MessageDto toDto(Message message);
}
