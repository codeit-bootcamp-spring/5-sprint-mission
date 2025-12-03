package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.ReadStatusDto.CreateCommand;
import com.sprint.mission.discodeit.dto.ReadStatusDto.CreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusDto.UpdateCommand;
import com.sprint.mission.discodeit.dto.ReadStatusDto.UpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;
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
                     .notificationEnabled(channel.getType()
                                                 .equals(ChannelType.PRIVATE))
                     .build();
  }

  public CreateCommand toCommand(CreateRequest request) {
    return CreateCommand.builder()
                        .userId(request.getUserId())
                        .channelId(request.getChannelId())
                        .lastReadAt(request.getLastReadAt())
                        .build();
  }

  public UpdateCommand toCommand(UUID id, UpdateRequest request) {
    return UpdateCommand.builder()
                        .id(id)
                        .lastReadAt(
                            request.getLastReadAt() != null ? request.getLastReadAt() : null)
                        .notificationEnabled(request.getNotificationEnabled() != null
                            ? request.getNotificationEnabled() : false)
                        .build();
  }
}
