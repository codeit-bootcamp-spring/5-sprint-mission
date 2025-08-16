package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.dto.response.readstatus.ReadStatusResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReadStatusMapper {
    public static ReadStatusResponse toReadStatusResponse(ReadStatus rs, UUID lastMessageId) {
        if (rs == null) return null;
        return new ReadStatusResponse(
                rs.getId(),
                rs.getCreatedAt(),
                rs.getUpdatedAt(),
                rs.getUserId(),
                rs.getChannelId(),
                rs.getLastReadMessageId(),
                lastMessageId == null || lastMessageId.equals(rs.getLastReadMessageId())
        );
    }
}
