// dto/data/ReadStatusDto.java
package com.sprint.mission.discodeit.dto.data;

public record ReadStatusDto(
        Long channelId,
        Long userId,
        Long lastReadMessageId,
        String lastReadAt,
        String notification // ON | MUTED 등
) {}
