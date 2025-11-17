package com.sprint.mission.discodeit.controller.api;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "User", description = "User API")
public interface UserApi {

	@Operation(summary = "User 등록")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨"),
		@ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
			content = @Content(examples = @ExampleObject(value = "User with email {email} already exists")))
	})
	ResponseEntity<UserDto> create(
		@Parameter(description = "User 생성 정보") @Valid UserCreateRequest userCreateRequest,
		@Parameter(description = "User 프로필 이미지") MultipartFile profile
	) throws IOException;

	@Operation(summary = "User 정보 수정")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨"),
		@ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
			content = @Content(examples = @ExampleObject(value = "user with email {newEmail} already exists"))),
		@ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
			content = @Content(examples = @ExampleObject(value = "User with id {userId} not found")))
	})
	ResponseEntity<UserDto> update(
		@Parameter(description = "수정할 User ID") UUID userId,
		@Parameter(description = "수정할 User 정보") @Valid UserUpdateRequest userUpdateRequest,
		@Parameter(description = "수정할 User 프로필 이미지") MultipartFile profile
	) throws IOException;

	@Operation(summary = "User 삭제")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
		@ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
			content = @Content(examples = @ExampleObject(value = "User with id {id} not found")))
	})
	ResponseEntity<Void> delete(
		@Parameter(description = "삭제할 User ID") UUID userId
	);

	@Operation(summary = "전체 User 목록 조회")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "User 목록 조회 성공")
	})
	ResponseEntity<List<UserDto>> findAll();

}
