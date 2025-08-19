package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data //getter & setter 자동 생성
public class PrivateChannelCreateRequest {
    private String name; // 채널 이름
    private String ownerId; // 채널 생성자 Id
    private List<String> membersId; // 초대한 멤버 목록


    // ✅ DTO → Channel 변환 메서드
    public Channel toEntity() {
        return new Channel(
                UUID.randomUUID(),
                "",
                ownerId,
                ChannelType.PRIVATE_CHANNEL,
                membersId
        );
    }

    // ✅ 고정된 UUID를 외부에서 주입받는 방식
    public Channel toEntityWithId(UUID fixedId) {
        return new Channel(
                fixedId, // ✅ 전달받은 UUID 사용
                "",
                ownerId,
                ChannelType.PRIVATE_CHANNEL,
                membersId
        );
    }
}

