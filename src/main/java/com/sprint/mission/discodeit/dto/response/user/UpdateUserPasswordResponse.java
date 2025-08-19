package com.sprint.mission.discodeit.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class UpdateUserPasswordResponse {
	private boolean success;
}
