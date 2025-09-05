package com.sprint.mission.discodeit.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import com.sprint.mission.discodeit.domain.response.PageResponse;

@Mapper(componentModel = "spring")
public interface PageResponseMapper {

	default <T> PageResponse<T> fromSlice(Slice<T> slice) {
		return PageResponse.<T>builder()
		  .content(slice.getContent())
		  .size(slice.getSize())
		  .hasNext(slice.hasNext())
		  .nextCursor(extractCursorFromPage(slice.getContent()))
		  .totalElements(slice.get().count())
		  .build();
	}

	default <T> PageResponse<T> fromPage(Page<T> page) {
		return PageResponse.<T>builder()
		  .content(page.getContent())
		  .size(page.getSize())
		  .hasNext(page.hasNext())
		  .nextCursor(extractCursorFromPage(page.getContent()))
		  .totalElements(page.getTotalElements())
		  .build();
	}

	default <T> T extractCursorFromPage(List<T> content) {
		if (content.isEmpty())
			return null;
		return content.get(content.size() - 1); // 마지막 요소 그대로 반환
	}

}
