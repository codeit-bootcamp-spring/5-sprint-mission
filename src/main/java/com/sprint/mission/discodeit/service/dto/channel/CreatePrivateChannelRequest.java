package com.sprint.mission.discodeit.service.dto.channel;

import java.util.List;
import java.util.UUID;

// PRIVATE 채널 생성 요청 DTO (참여자만 받음: name/description 없음)
public class CreatePrivateChannelRequest { // 클래스 선언
    public final List<UUID> participantUserIds; // 참여할 사용자 id 목록

    public CreatePrivateChannelRequest(List<UUID> participantUserIds) { // 생성자
        this.participantUserIds = participantUserIds; // 목록 세팅
    }
}
