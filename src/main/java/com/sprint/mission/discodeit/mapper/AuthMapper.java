package com.sprint.mission.discodeit.mapper;

import org.mapstruct.Mapper;

import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentResponse;
import com.sprint.mission.discodeit.domain.dto.jwt.JwtDto;
import com.sprint.mission.discodeit.domain.dto.response.JwtResponse;

@Mapper(componentModel = "spring")
public interface AuthMapper {
	JwtResponse toResponse(JwtDto jwtDto);

	BinaryContentResponse toResponse(BinaryContentDto dto);
}
