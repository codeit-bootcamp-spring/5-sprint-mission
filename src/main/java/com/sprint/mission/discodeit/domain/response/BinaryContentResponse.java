package com.sprint.mission.discodeit.domain.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class BinaryContentResponse {
	private final UUID id;
	private final String fileName;
	private final long size;
	private final String contentType;
	private final byte[] bytes;
}
