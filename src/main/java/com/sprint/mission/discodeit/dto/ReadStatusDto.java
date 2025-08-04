package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public class ReadStatusDto {

    /**
     * 채널의 읽지 않은 메시지 존재 여부를 나타냄
     */
    public record ChannelUnreadStatus(
            UUID channelId,
            boolean hasUnread
    ) {}
}
