package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
        UUID id,
        ChannelType type,
        String name,
        String description,
        List<UUID> participantIds,// 참여 User의 id 리스트
        Instant lastMessageAt // 해당 채널의 가장 마지막 메시지가 생성된 시간
) {}
