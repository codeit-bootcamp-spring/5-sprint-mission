package com.sprint.mission.discodeit.dto.request.userStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserStatusCreateRequest {
    @NotNull(message = "사용자 ID는 필수")
    private UUID userId;
    Instant lastActiveAt;
}