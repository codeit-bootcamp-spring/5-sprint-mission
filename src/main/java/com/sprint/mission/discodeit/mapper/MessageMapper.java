package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MessageMapper {

  private final BinaryContentMapper binaryContentMapper;
  private final UserMapper userMapper;
  private final UserStatusRepository userStatusRepository;

  public MessageResponseDto toDto(Message message) {
    if (message == null) return null;

    User author = message.getAuthor();
    UserStatus status = userStatusRepository.findByUserId(author.getId()).orElse(null);

    return new MessageResponseDto(
        message.getId(),
        message.getCreatedAt(),
        message.getUpdatedAt(),
        message.getContent(),
        message.getChannel().getId(),
        userMapper.toDto(author, status),
        message.getAttachments().stream()
            .map(binaryContentMapper::toDto)
            .toList()
    );
  }
}

