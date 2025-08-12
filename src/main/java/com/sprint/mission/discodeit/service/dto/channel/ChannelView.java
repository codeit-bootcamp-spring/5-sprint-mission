package com.sprint.mission.discodeit.service.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

// 채널 조회 응답 DTO (요구사항: 최신 메시지 시간, PRIVATE이면 참여자 id 포함)
public class ChannelView { // 클래스 선언
    public final UUID id; // 채널 id
    public final ChannelType type; // 채널 타입
    public final String name; // 이름(PUBLIC에서 주로 존재)
    public final String description; // 설명
    public final Instant createdAt; // 생성 시각
    public final Instant updatedAt; // 수정 시각
    public final Instant latestMessageAt; // 가장 최근 메시지 시각(없으면 null)
    public final List<UUID> participantUserIds; // PRIVATE일 때 참여자 id 목록(그 외 빈 리스트)

    public ChannelView(
                        UUID id,
                        ChannelType type,
                        String name,
                        String description,
                        Instant createdAt,
                        Instant updatedAt,
                        Instant latestMessageAt,
                        List<UUID> participantUserIds
    ) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.latestMessageAt = latestMessageAt;
        this.participantUserIds = participantUserIds;
    }
}
