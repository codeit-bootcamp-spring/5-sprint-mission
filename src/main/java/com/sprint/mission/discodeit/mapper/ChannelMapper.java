package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;

  public ChannelDto toDto(Channel channel) {
    Instant lastMessageAt = messageRepository
        .findTop1ByChannel_IdOrderByCreatedAtDesc(channel.getId())
        .map(Message::getCreatedAt)
        .orElse(Instant.MIN);

    List<UUID> participantIds = new ArrayList<>();
    if (channel.getType() == ChannelType.PRIVATE) {
      readStatusRepository.findAllByChannel_Id(channel.getId())
          .stream()
          .map(rs -> rs.getUser().getId())
          .forEach(participantIds::add);
    }

    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participantIds,
        lastMessageAt
    );
  }
}
