package com.sprint.mission.discodeit.dto.response.binaryContent;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class UserProfileImageCreateResponse {
	private UUID profileId;
	private String fileName;
	private String contentType;
	private Long size;
}
