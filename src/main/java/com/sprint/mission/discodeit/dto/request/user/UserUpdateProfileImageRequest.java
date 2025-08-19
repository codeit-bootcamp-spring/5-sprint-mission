package com.sprint.mission.discodeit.dto.request.user;

import com.sprint.mission.discodeit.dto.request.binaryContent.UserProfileImageCreateRequest;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserUpdateProfileImageRequest {
	@Nullable
	private UserProfileImageCreateRequest NewUserProfileImage;
}
