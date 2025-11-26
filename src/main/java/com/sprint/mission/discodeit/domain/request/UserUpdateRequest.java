package com.sprint.mission.discodeit.domain.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserUpdateRequest {

	private final String newUsername;
	private final String newEmail;
	private final String newPassword;

	@JsonCreator
	public UserUpdateRequest(
	  @JsonProperty("newUsername") String newUsername,
	  @JsonProperty("newEmail") String newEmail,
	  @JsonProperty("newPassword") String newPassword) {
		this.newUsername = newUsername;
		this.newEmail = newEmail;
		this.newPassword = newPassword;
	}
}
