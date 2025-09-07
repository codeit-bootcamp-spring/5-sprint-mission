package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MessageMapper {

    private final BinaryContentMapper binaryContentMapper;
    private final UserMapper userMapper;

    public MessageMapper(BinaryContentMapper binaryContentMapper, UserMapper userMapper) {
        this.binaryContentMapper = binaryContentMapper;
        this.userMapper = userMapper;
    }

    public MessageDto toDto(Message message) {
        return new MessageDto(
            message.getId(),
            message.getCreatedAt(),
            message.getUpdatedAt(),
            message.getContent(),
            message.getChannel().getId(),
            userMapper.toDto(message.getAuthor(), message.getAuthor().getStatus() != null ? message.getAuthor().getStatus().isOnline() : null),
            message.getAttachments().stream()
                .map(binaryContentMapper::toDto)
                .collect(Collectors.toList())
        );
    }
}
