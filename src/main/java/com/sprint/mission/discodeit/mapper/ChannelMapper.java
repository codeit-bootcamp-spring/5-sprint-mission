package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ChannelMapper {

  Channel toChannel(PublicChannelCreateRequest req);

  Channel toChannel(PrivateChannelCreateRequest req);
}
