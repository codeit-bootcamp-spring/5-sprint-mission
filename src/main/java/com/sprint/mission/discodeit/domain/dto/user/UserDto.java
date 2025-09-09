package com.sprint.mission.discodeit.domain.dto.user;

import java.util.UUID;

import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class UserDto {
	private UUID id;
	private String username;
	private String email;
	private BinaryContentDto profile;
	private Boolean online;
}
