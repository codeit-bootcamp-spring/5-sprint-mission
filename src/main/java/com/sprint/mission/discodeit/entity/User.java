package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class User {

	// createdAt, updateAt : Unix timeStamp
	private final UUID id;
	private final Long createdAt;
	private Long updatedAt;
	private String loginId;
	private String password;
	private String defaultNickname;

	public User(String loginId, String password, String defaultNickname) {
		this.loginId = loginId;
		this.password = password;
		this.defaultNickname = defaultNickname;

		id = UUID.randomUUID();
		createdAt = Instant.now().getEpochSecond();
		updatedAt = createdAt;
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
}
