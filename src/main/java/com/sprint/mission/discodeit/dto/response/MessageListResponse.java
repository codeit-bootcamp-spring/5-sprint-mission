package com.sprint.mission.discodeit.dto.response;

import java.util.List;

public record MessageListResponse(
        List<MessageResponseDto> messages
) {}
