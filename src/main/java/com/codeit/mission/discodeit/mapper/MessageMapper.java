package com.codeit.mission.discodeit.mapper;

import com.codeit.mission.discodeit.dto.data.BinaryContentDto;
import com.codeit.mission.discodeit.dto.data.MessageDto;
import com.codeit.mission.discodeit.entity.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {

    private final BinaryContentMapper binaryContentMapper;
    private final UserMapper userMapper;

    public MessageDto toDto(Message message) {
        List<BinaryContentDto> attachments = message.getAttachments().stream()
            .map(binaryContentMapper::toDto)
            .toList();

        return new MessageDto(message.getId(), message.getCreatedAt(), message.getUpdatedAt(),
            message.getContent(), message.getChannel().getId(),
            userMapper.toDto(message.getAuthor()), attachments);
    }
}
