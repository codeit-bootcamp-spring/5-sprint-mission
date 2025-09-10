package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class MessageMapper {

  private final BinaryContentMapper binaryContentMapper;
  private final UserMapper userMapper;

  public @Nullable MessageDto toDto(@Nullable Message message) {
    if (message == null) return null;

    // attachments: null/빈 컬렉션 안전
    List<BinaryContentDto> attachments =
        binaryContentMapper.toDtoList(message.getAttachments());

    // 채널/작성자 null 안전
    var channelId = (message.getChannel() != null) ? message.getChannel().getId() : null;

    return new MessageDto(
        message.getId(),
        message.getCreatedAt(),
        message.getUpdatedAt(),
        message.getContent(),
        channelId,
        userMapper.toDto(message.getAuthor()),  // author가 null이어도 안전하게 처리
        attachments
    );
  }
}
