package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {
    UserMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class NotificationMapper {

  @Mapping(target = "receiver", expression = "java(userMapper.toDetail(notification.getReceiver()))")
  public abstract NotificationDto.Detail toDetail(Notification notification);

  @Mapping(target = "receiver", expression = "java(userMapper.toDetailResponse(detail.getReceiver()))")
  public abstract NotificationDto.DetailResponse toDetailResponse(NotificationDto.Detail detail);
}
