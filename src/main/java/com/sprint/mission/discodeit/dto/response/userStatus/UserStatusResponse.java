package com.sprint.mission.discodeit.dto.response.userStatus;

import com.sprint.mission.discodeit.entity.UserStatus;
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
public class UserStatusResponse {
    private UUID id;
    private UUID userId;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastActiveAt;
    private boolean success;

    private UserStatusResponse(UserStatus userStatus) {
        this.id = userStatus.getId();
        this.userId = userStatus.getUser().getId();
        this.updatedAt = userStatus.getUpdatedAt();
        this.success = true;
    }

    public static UserStatusResponse success(UserStatus userStatus) {
        return new UserStatusResponse(userStatus);
    }
}