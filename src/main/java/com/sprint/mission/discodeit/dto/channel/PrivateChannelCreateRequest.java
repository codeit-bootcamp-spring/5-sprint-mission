package com.sprint.mission.discodeit.dto.channel;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data //getter & setter 자동 생성
public class PrivateChannelCreateRequest {

  private List<UUID> participantIds;

  private String name; // 채널 이름
  private String ownerId; // 채널 생성자 Id
  private String description; //채널 설명
  private List<String> membersId; // 초대한 멤버 목록


  // DTO → Channel 변환 메서드
  public Channel toEntity() {
    return new Channel(
        UUID.randomUUID(),
        name,
        ownerId,
        description,
        ChannelType.PRIVATE_CHANNEL,
        membersId
    );
  }

  //고정된 UUID를 외부에서 주입받는 방식
  public Channel toEntityWithId(UUID fixedId) {
    return new Channel(
        fixedId,
        name,
        ownerId,
        description,
        ChannelType.PRIVATE_CHANNEL,
        membersId
    );
  }
}


