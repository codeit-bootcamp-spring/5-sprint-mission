package com.sprint.mission.discodeit.linstener;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FailureNotificationEvent {
    private String taskName;
    private String requestId;
    private UUID binaryContentId;
    private String errorMessage;
}
