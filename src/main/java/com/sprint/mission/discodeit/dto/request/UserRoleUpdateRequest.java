package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.Role;

import lombok.Data;

@Data
public class UserRoleUpdateRequest {
	UUID userId;
	Role newRole;
}
