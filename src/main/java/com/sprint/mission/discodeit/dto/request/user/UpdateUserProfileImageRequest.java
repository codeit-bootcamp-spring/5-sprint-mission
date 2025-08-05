package com.sprint.mission.discodeit.dto.request.user;

import java.util.UUID;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateUserProfileImageRequest {
	private UUID id;
	@Nullable
	private CreateUserProfileImageRequest userProfileImage;
}
