package com.sprint.mission.discodeit.dto.readstatus;

import com.sprint.mission.discodeit.entity.ReadStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ReadStatusResponse(
        UUID id,
        UUID userId,
        UUID channelId,
        Instant createdAt,
        Instant updatedAt,
        Instant lastReadAt
) {
    public static  ReadStatusResponse of(ReadStatus readStatus){
        return ReadStatusResponse.builder()
                .id(readStatus.getId())
                .userId(readStatus.getUserId())
                .channelId(readStatus.getChannelId())
                .createdAt(readStatus.getCreatedAt())
                .updatedAt(readStatus.getUpdatedAt())
                .lastReadAt(readStatus.getLastReadAt())
                .build();
    }
}
