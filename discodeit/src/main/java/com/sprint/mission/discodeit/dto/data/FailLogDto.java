package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;


public record  FailLogDto(
        String requstId,
        String binaryContentId,
        String error

) {
}
