package com.sprint.mission.discodeit.domain.dto;

import com.sprint.mission.discodeit.domain.request.UserCreateRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CreateUserDTO {
	private String username;
	private String email;
	private String password;
	private CreateBiContentDTO binaryContent;

	public static CreateUserDTO from(UserCreateRequest request, CreateBiContentDTO profileImage) {
		return new CreateUserDTO(request.getUsername(), request.getEmail(), request.getPassword(), profileImage);
	}

}
