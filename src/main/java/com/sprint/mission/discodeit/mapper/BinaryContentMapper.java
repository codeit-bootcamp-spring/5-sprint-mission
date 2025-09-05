package com.sprint.mission.discodeit.mapper;

import org.mapstruct.Mapper;

import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentResponse;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

	BinaryContentDto toDto(BinaryContent content);

	BinaryContentResponse toResponse(BinaryContentDto dto);

}
