package com.sprint.mission.discodeit.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PageResponse<T>(
	List<T> content,
	Object nextCursor,
	int size,
	boolean hasNext,
	Long totalElements
) {

}
