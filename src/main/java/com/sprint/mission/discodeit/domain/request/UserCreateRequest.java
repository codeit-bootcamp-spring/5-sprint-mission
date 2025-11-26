package com.sprint.mission.discodeit.domain.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class UserCreateRequest {
	@NotBlank(message = "username은 비어있을 수 없습니다.")
	private final String username;
	@NotBlank(message = "email은 비어있을 수 없습니다.")
	private final String email;
	@NotBlank(message = "password은 비어있을 수 없습니다.")
	private final String password;

	@JsonCreator
	public UserCreateRequest(
	  @JsonProperty("username") String username,
	  @JsonProperty("email") String email,
	  @JsonProperty("password") String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}
}
