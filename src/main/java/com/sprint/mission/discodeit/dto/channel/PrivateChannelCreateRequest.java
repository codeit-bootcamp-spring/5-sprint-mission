package com.sprint.mission.discodeit.dto.channel;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.UUID;
import lombok.Data;

@Data
public class PrivateChannelCreateRequest {

  private String name;
  private String description;

  public Channel toEntity() {
    return new Channel(
        name,
        description,
        ChannelType.PRIVATE
    );
  }

  public Channel toEntityWithId(UUID fixedId) {
    Channel channel = new Channel(
        name,
        description,
        ChannelType.PRIVATE
    );
    channel.setId(fixedId);
    return channel;
  }
}


