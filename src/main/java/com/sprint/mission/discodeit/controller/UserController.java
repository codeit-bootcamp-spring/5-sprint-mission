package com.sprint.mission.discodeit.controller;

import static com.sprint.mission.discodeit.service.basic.BasicUserService.*;
import static com.sprint.mission.discodeit.service.basic.BasicUserStatusService.*;
import static org.springframework.http.MediaType.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sprint.mission.discodeit.domain.dto.CreateBiContentDTO;
import com.sprint.mission.discodeit.domain.dto.CreateUserDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateStatusByUserIdDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateUserDTO;
import com.sprint.mission.discodeit.domain.dto.UserUpdateResult;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.request.CreateUserResponse;
import com.sprint.mission.discodeit.domain.request.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.domain.request.UserCreateRequest;
import com.sprint.mission.discodeit.domain.request.UserUpdateRequest;
import com.sprint.mission.discodeit.domain.response.UpdateUserStatusByUserIdResponse;
import com.sprint.mission.discodeit.domain.response.UserDeleteResponse;
import com.sprint.mission.discodeit.domain.response.UserReadResponse;
import com.sprint.mission.discodeit.domain.response.UserUpdateResponse;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "User API")
public class UserController {

	private final UserService userService;
	private final UserStatusService userStatusService;

	@PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CreateUserResponse> createUser(
	  @RequestPart @Valid UserCreateRequest userCreateRequest,
	  @RequestPart(required = false) MultipartFile profile

	) throws IOException {
		Optional<CreateBiContentDTO> biContentDTO = Optional.empty();
		if (profile != null && !profile.isEmpty()) {

			biContentDTO = Optional.of(new CreateBiContentDTO(
			  profile.getBytes(),
			  profile.getSize(),
			  profile.getContentType(),
			  profile.getOriginalFilename()
			));
		}
		User createdUser = userService.create(CreateUserDTO.builder()
		  .username(userCreateRequest.getUsername())
		  .email(userCreateRequest.getEmail())
		  .password(userCreateRequest.getPassword())
		  .binaryContent(biContentDTO.orElse(null))
		  .build());

		URI location = URI.create("api/users");
		return ResponseEntity.created(location).body(toCreateUserResponse(createdUser));
	}

	@GetMapping
	public ResponseEntity<List<UserReadResponse>> getAllUser() {

		List<UserReadResponse> body = userService.readAll().stream()
		  .map(BasicUserService::toUserReadResponse)
		  .toList();

		return ResponseEntity.ok(body);
	}

	@PatchMapping(value = "/{id}", consumes = MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UserUpdateResponse> updateUser(
	  @RequestPart @Valid UserUpdateRequest userUpdateRequest,
	  @RequestPart(required = false) MultipartFile profile,
	  @PathVariable UUID id
	) throws IOException {

		Optional<CreateBiContentDTO> biContentDTO = Optional.empty();
		if (profile != null && !profile.isEmpty()) {

			biContentDTO = Optional.of(new CreateBiContentDTO(
			  profile.getBytes(),
			  profile.getSize(),
			  profile.getContentType(),
			  profile.getOriginalFilename()
			));
		}

		UserUpdateResult result = userService.update(UpdateUserDTO.builder()
		  .userId(id)
		  .newUsername(userUpdateRequest.getNewUsername())
		  .newEmail(userUpdateRequest.getNewEmail())
		  .newPassword(userUpdateRequest.getNewPassword())
		  .newProfilePicture(biContentDTO.orElse(null))
		  .build());

		return ResponseEntity.ok(toUserUpdateResponse(result));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<UserDeleteResponse> deleteUser(@PathVariable UUID id) {
		userService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{userId}/userStatus")
	public ResponseEntity<UpdateUserStatusByUserIdResponse> updateUserStatus(
	  @PathVariable UUID userId,
	  @RequestBody UpdateUserStatusRequest updateUserStatusRequest) {

		UserStatus newUserStatus = userStatusService.updateStatusByUserId(UpdateStatusByUserIdDTO.builder()
		  .userId(userId)
		  .newLastActiveAt(updateUserStatusRequest.getNewLastActiveAt())
		  .build());

		return ResponseEntity.ok(toUpdateUserStatusByUserIdResponse(newUserStatus));
	}

}
