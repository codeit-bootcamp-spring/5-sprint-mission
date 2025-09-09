package com.sprint.mission.discodeit.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.dto.user.UserResponse;
import com.sprint.mission.discodeit.domain.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

	@Mapping(source = "user.id", target = "id")
	@Mapping(source = "user.username", target = "username")
	@Mapping(source = "user.email", target = "email")
		// profile, online은 수동으로 세팅
	UserDto toDto(User user, @Context boolean isOnline, @Context BinaryContentDto userProfile);

	// AfterMapping 훅에서 추가 파라미터 활용
	@AfterMapping
	default void mapExtraFields(User user,
	  @Context boolean isOnline,
	  @Context BinaryContentDto userProfile,
	  @MappingTarget UserDto.UserDtoBuilder dto) {
		dto.online(isOnline);
		dto.profile(userProfile);
	}

	public UserResponse toResponse(UserDto userDto);
}
