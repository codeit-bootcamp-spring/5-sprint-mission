package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ChannelMapper {
  ChannelDto toDto(Channel channel);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "participantIds", ignore = true)
  Channel toChannel(PublicChannelCreateRequest req);
  Channel toChannel(PrivateChannelCreateRequest req);
  void updateEntityFromRequest(ChannelUpdateRequest req, @MappingTarget Channel channel);

//UUID id,
//        ChannelType type,
//        String name,
//        String description,
//        List<UserDto> participants,
//        Instant lastMessageAt

}
