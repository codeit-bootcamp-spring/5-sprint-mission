package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class User implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private final UUID id;
	private final Instant createdAt;
	private Instant updatedAt;
	private String email;
	private String loginId;
	private String password;
	private String defaultNickname;
	@Nullable
	private UUID profileId;

	public User(String loginId, String password, String defaultNickname, String email, @Nullable UUID profileId) {
		this.loginId = loginId;
		this.password = password;
		this.defaultNickname = defaultNickname;
		this.email = email;
		this.profileId = profileId;

		id = UUID.randomUUID();
		createdAt = Instant.now();
		updatedAt = createdAt;
	}

	// 복사용
	public User(User original) {
		this.id = original.id;
		this.createdAt = original.createdAt;
		this.updatedAt = original.updatedAt;
		this.email = original.email;
		this.profileId = original.profileId;
		this.loginId = original.loginId;
		this.password = original.password;
		this.defaultNickname = original.defaultNickname;
	}

	public void updateUpdatedAt() {
		this.updatedAt = Instant.now();
	}

	public void updateEmail(String email) {
		updateUpdatedAt();
		this.email = Objects.requireNonNull(email, "이메일은 필수 입력값입니다.");
	}

	public void updateProfileId(@Nullable UUID profileId) {
		updateUpdatedAt();
		this.profileId = profileId;
	}

	public void updatePassword(String password) {
		updateUpdatedAt();
		this.password = Objects.requireNonNull(password, "비밀번호는 필수 입력값입니다.");
	}

	public void updateDefaultNickname(String defaultNickname) {
		updateUpdatedAt();
		this.defaultNickname = Objects.requireNonNull(defaultNickname, "닉네임은 필수 입력값입니다.");
	}

	public void removeProfile() {
		updateUpdatedAt();
		this.profileId = null;
	}

	public User copy() {
		return new User(this);
	}
}
