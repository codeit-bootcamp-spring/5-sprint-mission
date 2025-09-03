package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ChannelMapper {

  @Autowired
  protected UserMapper userMapper;

  @Mapping(target = "lastMessageAt", expression = "java(channel.getMessages().stream()"
      + "    .max(java.util.Comparator.comparing(c -> c.getCreatedAt()))"
      + "    .map(c -> c.getCreatedAt())" + "    .orElse(null))")
  @Mapping(target = "participants", expression = "java(toParticipantDetails(channel.getReadStatuses()))")
  public abstract ChannelDto.Detail toDetail(Channel channel);

  public abstract ChannelDto.DetailResponse toDetailResponse(ChannelDto.Detail detail);

  protected List<UserDto.Detail> toParticipantDetails(List<ReadStatus> readStatuses) {
    
    return readStatuses.stream()
                       .map(ReadStatus::getUser)
                       .map(userMapper::toDetail)
                       .distinct()
                       .toList();
  }
}
