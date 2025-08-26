package com.sprint.mission.discodeit.dto.response.user;

import java.time.Instant;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDeleteResponse {
	private UUID id;
	private String username; // loginId 임
	private String nickname; // defaultNickname 임
	private String email;
	private Instant createdAt;
	private Instant updatedAt;

	private UserDeleteResponse(User user) {
		this.id = user.getId();
		this.nickname = user.getDefaultNickname();
		this.email = user.getEmail();
		this.createdAt = user.getCreatedAt();
		this.updatedAt = user.getUpdatedAt();
		this.username = user.getUsername(); // loginId는 username으로
	}

	public static UserDeleteResponse success(User user) {
		return new UserDeleteResponse(user);
	}
}
