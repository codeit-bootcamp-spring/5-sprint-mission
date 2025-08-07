package com.sprint.mission.discodeit.dto.request.user;

import java.time.Instant;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.binaryContent.CreateUserProfileImageRequest;
import com.sprint.mission.discodeit.entity.User;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateUserRequest {
	private String loginId;
	private String password;
	private String defaultNickname;
	private String email;
	@Nullable
	private CreateUserProfileImageRequest profileImage;

	public User toUser() {
		return toUserWithProfile(null);
	}

	public User toUserWithProfile(UUID profileId){
		Instant now = Instant.now();

		return User.builder()
				.id(UUID.randomUUID())
				.createdAt(now)
				.updatedAt(now)
				.profileId(profileId)
				.loginId(loginId)
				.password(password)
				.defaultNickname(defaultNickname)
				.email(email)
				.build();
	}

}
