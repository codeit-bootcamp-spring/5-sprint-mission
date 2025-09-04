package com.sprint.mission.discodeit.dto.channel;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Data;

@Data
public class PrivateChannelCreateRequest {

  private String name;
  private String description;
  private ChannelType channelType;

  public Channel toEntity() {
    return new Channel(
        name,
        description,
        channelType
    );
  }
}


