package com.sprint.mission.discodeit.dto.response.user;

import java.time.Instant;
import java.util.UUID;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateUserResponse {
	private UUID id;
	private Instant createdAt;
	private Instant updatedAt;
	private String email;
	private String defaultNickname;
	@Nullable
	private UUID profileId;
	private boolean success;
}
