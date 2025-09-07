package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChannelMapper {
  public ChannelResponseDto toDto(Channel channel) {
    if (channel == null) return null;

    return new ChannelResponseDto(
        channel.getId(),
        channel.getCreatedAt(),
        channel.getUpdatedAt(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        extractParticipantIds(channel),
        extractLastMessageAt(channel)
    );
  }

  private List<UUID> extractParticipantIds(Channel channel) {
    List<UUID> participantIds = new ArrayList<>();

    if (channel.getType() == ChannelType.PRIVATE) {
      return channel.getReadStatuses().stream()
          .map(rs -> rs.getUser().getId())
          .distinct()
          .toList();
    }
    return participantIds;
  }

  private Instant extractLastMessageAt(Channel channel) {
    return channel.getMessages().stream()
        .map(Message::getCreatedAt)
        .max(Instant::compareTo)
        .orElse(null);
  }
}
