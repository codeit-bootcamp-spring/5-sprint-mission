package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

  private final UserMapper userMapper;

  public ChannelDto.Detail toDetail(Channel channel) {
    if (channel == null) {
      return null;
    }

    return ChannelDto.Detail.builder()
                            .id(channel.getId())
                            .name(channel.getName())
                            .type(channel.getType())
                            .description(channel.getDescription())
                            .lastMessageAt(channel.getMessages()
                                                  .stream()
                                                  .max(Comparator.comparing(
                                                      BaseEntity::getCreatedAt))
                                                  .map(BaseEntity::getCreatedAt)
                                                  .orElse(null))
                            .participants(channel.getReadStatuses()
                                                 .stream()
                                                 .map(ReadStatus::getUser)
                                                 .map(userMapper::toDetail)
                                                 .distinct()
                                                 .toList())
                            .build();
  }
}
