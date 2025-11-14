package com.sprint.mission.discodeit.domain.dto.command;

import java.util.UUID;

import com.sprint.mission.discodeit.domain.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.domain.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateRoleCommand {
	private Role role;
	private UUID userId;

	public static UpdateRoleCommand of(UserRoleUpdateRequest request) {
		return new UpdateRoleCommand(request.getRole(), request.getUserId());
	}

}
