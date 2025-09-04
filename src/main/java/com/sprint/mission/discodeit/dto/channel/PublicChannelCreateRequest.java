package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Data;

@Data // getter & setter 자동 생성
public class PublicChannelCreateRequest { // 공개채널 DTO

  private String name;
  private String description;
  private ChannelType channelType;

  //DTO -> Channel 변환 메서드
  public Channel toEntity() {
    return new Channel(
        name,
        description,
        channelType
    );
  }
}
