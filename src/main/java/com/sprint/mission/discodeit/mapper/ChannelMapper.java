package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelDto.CreateCommand;
import com.sprint.mission.discodeit.dto.ChannelDto.CreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto.UpdateCommand;
import com.sprint.mission.discodeit.dto.ChannelDto.UpdateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
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
                       .map(user -> userMapper.toDetail(user, false))
                       .distinct()
                       .toList();
  }

  public CreateCommand toCommand(CreateRequest request, ChannelType type) {
    return CreateCommand.builder()
                        .type(type)
                        .name(request.getName())
                        .description(request.getDescription())
                        .participantIds(request.getParticipantIds())
                        .build();
  }

  public UpdateCommand toCommand(UpdateRequest request, UUID id) {
    return UpdateCommand.builder()
                        .id(id)
                        .name(request.getName())
                        .description(request.getDescription())
                        .participantIds(request.getParticipantIds())
                        .build();
  }
}
