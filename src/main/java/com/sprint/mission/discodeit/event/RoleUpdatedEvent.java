package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record RoleUpdatedEvent(
	String oldRole,
	String newRole,
	UUID userId
) {
}
