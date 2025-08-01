package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

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
	private String loginId;
	private String password;
	private String nickname;
	private String email;
	@Nullable
	private UserProfileImageDTO profileImage;

	public User toUser(){
		Instant now = Instant.now();

		return User.builder()
				.id(UUID.randomUUID())
				.createdAt(now)
				.updatedAt(now)
				.loginId(loginId)
				.password(password)
				.defaultNickname(nickname)
				.email(email)
				.build();
	}

}
