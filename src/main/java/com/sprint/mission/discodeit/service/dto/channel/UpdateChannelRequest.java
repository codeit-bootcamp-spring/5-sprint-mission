package com.sprint.mission.discodeit.service.dto.channel;

import java.util.UUID;

// 채널 수정 요청 DTO (PRIVATE 수정 불가 규칙은 서비스에서 검증)
public class UpdateChannelRequest { // 클래스 선언
    public final UUID channelId; // 수정 대상 채널 id
    public final String newName; // 새 이름(null이면 미변경)
    public final String newDescription; // 새 설명(null이면 미변경)

    public UpdateChannelRequest(UUID channelId, String newName, String newDescription) {
        this.channelId = channelId;
        this.newName = newName;
        this.newDescription = newDescription;
    }
}
