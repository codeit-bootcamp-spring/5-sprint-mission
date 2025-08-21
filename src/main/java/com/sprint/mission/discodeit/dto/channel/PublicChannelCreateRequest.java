package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.ArrayList;
import java.util.UUID;
import lombok.Data;

@Data // getter & setter 자동 생성
public class PublicChannelCreateRequest { // 공개채널 DTO

  private String name;
  private String description;
  
  private String ownerId; // 추후 삭제 예정

  //DTO -> Channel 변환 메서드
  public Channel toEntity() {
    return new Channel(
        UUID.randomUUID(),
        name,
        ownerId,
        ChannelType.PUBLIC_CHANNEL,
        new ArrayList<>() // 공개 채널은 멤버 없음
    );
  }
}
