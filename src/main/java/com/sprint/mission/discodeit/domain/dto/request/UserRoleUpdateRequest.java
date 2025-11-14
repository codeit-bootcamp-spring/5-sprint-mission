package com.sprint.mission.discodeit.domain.dto.request;

import java.util.UUID;

import com.sprint.mission.discodeit.domain.enums.Role;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserRoleUpdateRequest {
	@NotNull(message = "userId는 Null 허용되지 않습니다.")
	private UUID userId;
	private Role role;
}
