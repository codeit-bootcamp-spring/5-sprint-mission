package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ChannelMapper {

  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserMapper userMapper;

  @Transactional(readOnly = true)
  public ChannelDto toDto(Channel channel) {
    // 최근 메시지 시간
    Instant lastMessageAt =
        messageRepository
            .findAllByChannel_Id(
                channel.getId(),
                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt"))
            )
            .getContent()
            .stream()
            .map(Message::getCreatedAt)
            .findFirst()
            .orElse(Instant.MIN);

    // PRIVATE 일때만 참가자(UserDto) 조회
    List<UserDto> participants = new ArrayList<>();
    if (channel.getType() == ChannelType.PRIVATE) {
      readStatusRepository.findAllByChannel_Id(channel.getId())
          .stream()
          .map(rs -> userMapper.toDto(rs.getUser())) // online 은 여기선 null
          .forEach(participants::add);
    }

    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participants,
        lastMessageAt
    );
  }
}
