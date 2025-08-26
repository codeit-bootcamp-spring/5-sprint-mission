package com.sprint.mission.discodeit.dto.readstatus;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

public class ReadStatusDto {

    public record create(
            @NotNull(message = "사용자 아이디를 입력하세요")
            UUID userId,
            @NotNull(message = "채널 아이디를 입력하세요")
            UUID channelId,
            @NotNull(message = "메시지 아이디를 입력하세요")
            UUID messageId
    ) {}

    @Builder
    public record unread(
            UUID channelId,
            boolean hasUnread
    ) {}

    @Builder
    public record response(
            UUID userId,
            UUID channelId,
            String readAt
    ) {}
}
