package com.sprint.mission.discodeit.dto.request.user;

import java.time.Instant;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.binaryContent.UserProfileImageRequest;
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
public class UserCreateRequest {
	private String username;
	private String password;
	private String defaultNickname;
	private String email;
	@Nullable
	private UserProfileImageRequest profileImage;

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
				.username(username)
				.password(password)
				.defaultNickname(defaultNickname)
				.email(email)
				.build();
	}

}
