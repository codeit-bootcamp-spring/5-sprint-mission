package com.sprint.mission.discodeit.domain.dto.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.domain.enums.Role;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserRoleUpdateRequest {
	@NotNull(message = "userId는 Null 허용되지 않습니다.")
	private final UUID userId;
	private final Role newRole;

	@JsonCreator
	public UserRoleUpdateRequest(
	  @JsonProperty("userId") UUID userId, @JsonProperty("newRole") Role newRole) {
		this.userId = userId;
		this.newRole = newRole;
	}
}
