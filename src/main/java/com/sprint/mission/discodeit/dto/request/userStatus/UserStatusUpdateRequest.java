package com.sprint.mission.discodeit.dto.request.userStatus;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class UserStatusUpdateRequest {
	Instant newLastActiveAt;
}