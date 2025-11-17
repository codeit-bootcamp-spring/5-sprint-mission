package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.Role;

public record UserRoleUpdateRequest(
	UUID userId,
	Role newRole
) {
}
