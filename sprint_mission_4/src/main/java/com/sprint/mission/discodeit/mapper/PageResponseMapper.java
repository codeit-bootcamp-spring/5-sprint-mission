package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

@Mapper(componentModel = "spring")
public interface PageResponseMapper {
  <T>PageResponse<T> fromSlice(Slice<T> slice);
  <T>PageResponse<T> fromPage(Page<T> page);

}
