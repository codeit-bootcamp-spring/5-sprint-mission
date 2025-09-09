package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.ReadStatusDto.CreateCommand;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ReadStatusMapper {

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "channelId", source = "channel.id")
  public abstract ReadStatusDto.Detail toDetail(ReadStatus readStatus);

  public abstract ReadStatusDto.DetailResponse toDetailResponse(ReadStatusDto.Detail detail);

  public ReadStatus toEntity(CreateCommand create, User user, Channel channel) {
    return ReadStatus.builder()
                     .user(user)
                     .channel(channel)
                     .lastReadAt(create.getLastReadAt() != null ? create.getLastReadAt()
                         : channel.getCreatedAt())
                     .build();
  }
}
