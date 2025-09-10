package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = { UserMapper.class, BinaryContentMapper.class }
)
public interface MessageMapper {

    @Mapping(source = "message.id", target = "id")
    @Mapping(source = "message.channel.id", target = "channelId")
    MessageDto toDto(Message message, List<BinaryContent> attachments);

    @Mapping(source = "message.id", target = "id")
    @Mapping(source = "message.channel.id", target = "channelId")
    @Mapping(source = "author", target = "author")
    MessageDto toDtoWithAuthorDto(Message message, UserDto author, List<BinaryContent> attachments);
}
