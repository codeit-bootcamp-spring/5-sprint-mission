package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ChannelMapper {

  @Autowired
  protected UserMapper userMapper;

  @Mapping(target = "lastMessageAt", expression = "java(toLastMessageAt(channel.getMessages()))")
  @Mapping(target = "participants", expression = "java(toParticipantDetails(channel.getReadStatuses()))")
  public abstract ChannelDto.Detail toDetail(Channel channel);

  public abstract ChannelDto.DetailResponse toDetailResponse(ChannelDto.Detail detail);

  protected Instant toLastMessageAt(List<Message> messages) {
    return messages.stream()
                   .max(Comparator.comparing(m -> m.getCreatedAt()))
                   .map(BaseEntity::getCreatedAt)
                   .orElse(null);
  }

  protected List<UserDto.Detail> toParticipantDetails(List<ReadStatus> readStatuses) {

    return readStatuses.stream()
                       .map(ReadStatus::getUser)
                       .map(userMapper::toDetail)
                       .distinct()
                       .toList();
  }
}
