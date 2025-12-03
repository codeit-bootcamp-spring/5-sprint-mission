package com.sprint.mission.discodeit.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class S3UploadFailedEvent {
    private final UUID binaryContentId;
    private final String requestId;
    private final String errorMessage;
}
