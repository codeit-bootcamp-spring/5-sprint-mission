package com.sprint.mission.discodeit.dto.request.readStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReadStatusUpdateRequest {
    private Instant newLastReadAt;
}
