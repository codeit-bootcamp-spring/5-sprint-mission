package com.sprint.mission.discodeit.security.dto;

import com.sprint.mission.discodeit.dto.data.UserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtDto {
	String accessToken;
	UserDto userDto;

}
