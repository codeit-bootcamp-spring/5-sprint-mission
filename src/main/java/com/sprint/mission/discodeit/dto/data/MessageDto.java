// dto/data/MessageDto.java
package com.sprint.mission.discodeit.dto.data;

import java.util.List;

public record MessageDto(
        Long id,
        Long channelId,
        UserDto sender,
        String content,
        List<BinaryContentDto> attachments,
        Boolean deleted,
        String createdAt,
        String editedAt
) {}
