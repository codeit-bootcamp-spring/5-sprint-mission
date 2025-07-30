package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class User implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	// createdAt, updateAt : Unix timeStamp
	private final UUID id;
	private final Long createdAt;
	private Long updatedAt;
	private String loginId;
	private String password;
	private String defaultNickname;

	public User(String loginId, String password, String defaultNickname) {
		this.loginId = Objects.requireNonNull(loginId, "로그인ID는 필수입니다");
		this.password = Objects.requireNonNull(password, "비밀번호는 필수입니다");
		this.defaultNickname = Objects.requireNonNull(defaultNickname, "닉네임은 필수입니다");

		id = UUID.randomUUID();
		createdAt = Instant.now().getEpochSecond();
		updatedAt = createdAt;
	}

	// 복사용
	public User(User original) {
		this.id = original.id;
		this.createdAt = original.createdAt;
		this.updatedAt = original.updatedAt;
		this.loginId = original.loginId;
		this.password = original.password;
		this.defaultNickname = original.defaultNickname;
	}

	public UUID getId() {
		return id;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public Long getUpdatedAt() {
		return updatedAt;
	}

	public String getLoginId() {
		return loginId;
	}

	public String getPassword() {
		return password;
	}

	public String getDefaultNickname() {
		return defaultNickname;
	}

	public void updateUpdatedAt() {
		this.updatedAt = Instant.now().getEpochSecond();;
	}

	public void updateLoginId(String loginId) {
		this.loginId = loginId;
	}

	public void updatePassword(String password) {
		this.password = password;
	}

	public void updateDefaultNickname(String defaultNickname) {
		this.defaultNickname = defaultNickname;
	}

	public User copy() {
		return new User(this);
	}
}
