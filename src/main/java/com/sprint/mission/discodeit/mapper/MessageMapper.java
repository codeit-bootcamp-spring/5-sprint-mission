package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.data.MessageDto;
import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageMapper {

    private final UserMapper userMapper;
    private final BinaryContentMapper binaryContentMapper;

    public MessageDto toDto(Message message, List<BinaryContent> attachments) {
        if (message == null) {
            return null;
        }

        return new MessageDto(
            message.getId(),
            message.getCreatedAt(),
            message.getUpdatedAt(),
            message.getContent(),
            message.getChannel().getId(),
            userMapper.toDto(message.getAuthor()),
            attachments.stream().map(binaryContentMapper::toDto).toList()
        );
    }

    public MessageDto toDtoWithAuthorDto(Message message, UserDto author, List<BinaryContent> attachments) {
        if (message == null) {
            return null;
        }

        return new MessageDto(
            message.getId(),
            message.getCreatedAt(),
            message.getUpdatedAt(),
            message.getContent(),
            message.getChannel().getId(),
            author,
            attachments.stream().map(binaryContentMapper::toDto).toList()
        );
    }
}
