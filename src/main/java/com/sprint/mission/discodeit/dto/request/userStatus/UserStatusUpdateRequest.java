package com.sprint.mission.discodeit.dto.request.userStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserStatusUpdateRequest {
    Instant newLastActiveAt;
}