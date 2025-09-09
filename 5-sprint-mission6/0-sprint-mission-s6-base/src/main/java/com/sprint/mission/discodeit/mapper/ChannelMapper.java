package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserMapper userMapper;

  public ChannelDto toDto(Channel c) {
    if (c == null) return null;

    // 마지막 메시지 시각
    Instant lastMessageAt = messageRepository
        .findTop1ByChannel_IdOrderByCreatedAtDesc(c.getId())
        .map(Message::getCreatedAt)
        .orElse(Instant.MIN);

    // 참가자 (PRIVATE만 채움, PUBLIC은 빈 리스트)
    List<UserDto> participants =
        (c.getType() == ChannelType.PRIVATE)
            ? readStatusRepository.findByChannel_Id(c.getId()).stream()
            .map(rs -> userMapper.toDto(rs.getUser()))
            .toList()
            : Collections.emptyList();

    return new ChannelDto(
        c.getId(),
        c.getType(),
        c.getName(),
        c.getDescription(),
        participants,
        lastMessageAt
    );
  }
}