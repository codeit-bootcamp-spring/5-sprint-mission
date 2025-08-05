package com.sprint.mission.discodeit.dto.response.user;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class CreateUserProfileImageResponse {
	private UUID profileId;
	private String filename;
	private String contentType;
	private Long size;
}
