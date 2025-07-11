package com.sprint.mission.discodeit.entity;

public enum ROLE {
	ADMIN(1, "관리자"), USER(0, "일반사용자");

	private final int value;
	private final String role;

	private ROLE(int value, String role) {
		this.value = value;
		this.role = role;
	}

	public int getValue() {
		return value;
	}

	public String getAuthority() {
		return role;
	}

}
