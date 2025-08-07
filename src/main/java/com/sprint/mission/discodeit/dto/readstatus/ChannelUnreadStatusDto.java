package com.sprint.mission.discodeit.dto.readstatus;

import org.springframework.lang.NonNull;

import java.util.UUID;

public record ChannelUnreadStatusDto(
        UUID channelId,
        boolean hasUnread
) {
    @Override
    public @NonNull String toString() {
        return """
                채널목록 {
                    채널아이디: %s
                    읽음상태: %b
                }
                """.formatted(
                channelId,
                hasUnread
        );
    }
}
