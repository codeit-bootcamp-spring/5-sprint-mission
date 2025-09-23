package com.sprint.mission.discodeit.controller;

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
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.dto.user.UserResponse;
import com.sprint.mission.discodeit.domain.dto.userStatus.UserStatusDto;
import com.sprint.mission.discodeit.domain.dto.userStatus.UserStatusResponse;
import com.sprint.mission.discodeit.domain.request.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.domain.request.UserCreateRequest;
import com.sprint.mission.discodeit.domain.request.UserUpdateRequest;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
@Tag(name = "User", description = "User API")
public class UserController {

	private final UserService userService;
	private final UserStatusService userStatusService;
	private final UserMapper userMapper;
	private final UserStatusMapper userStatusMapper;

	@PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UserResponse> createUser(
	  @RequestPart @Valid UserCreateRequest userCreateRequest,
	  @RequestPart(required = false) MultipartFile profile

	) throws IOException {
		log.debug("Request to create user started. username={}", userCreateRequest.getUsername());
		Optional<CreateBiContentDTO> biContentDTO = Optional.empty();
		if (profile != null && !profile.isEmpty()) {
			log.debug("Profile file uploaded. name={}, size={}, type={}",
			  profile.getOriginalFilename(), profile.getSize(), profile.getContentType());

			biContentDTO = Optional.of(new CreateBiContentDTO(
			  profile.getBytes(),
			  profile.getSize(),
			  profile.getContentType(),
			  profile.getOriginalFilename()
			));
		}

		UserDto createdUser = userService.create(CreateUserDTO.builder()
		  .username(userCreateRequest.getUsername())
		  .email(userCreateRequest.getEmail())
		  .password(userCreateRequest.getPassword())
		  .binaryContent(biContentDTO.orElse(null))
		  .build());

		URI location = URI.create("api/users");
		log.debug("URI location={} in username={}", location, userCreateRequest.getUsername());

		log.debug("user created Request successfully done. username={}", userCreateRequest.getUsername());
		return ResponseEntity.created(location).body(userMapper.toResponse(createdUser));
	}

	@GetMapping
	public ResponseEntity<List<UserResponse>> getAllUser() {

		List<UserResponse> body = userService.readAll().stream()
		  .map(userMapper::toResponse)
		  .toList();

		return ResponseEntity.ok(body);
	}

	@PatchMapping(value = "/{id}", consumes = MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UserResponse> updateUser(
	  @RequestPart UserUpdateRequest userUpdateRequest,
	  @RequestPart(required = false) MultipartFile profile,
	  @PathVariable UUID id
	) throws IOException {
		log.debug("Request to update user started. newUsername={}", userUpdateRequest.getNewUsername());

		Optional<CreateBiContentDTO> biContentDTO = Optional.empty();
		if (profile != null && !profile.isEmpty()) {
			log.debug("New Profile file uploaded. name={}, size={}, type={}",
			  profile.getOriginalFilename(), profile.getSize(), profile.getContentType());

			biContentDTO = Optional.of(new CreateBiContentDTO(
			  profile.getBytes(),
			  profile.getSize(),
			  profile.getContentType(),
			  profile.getOriginalFilename()
			));
		}

		UserDto result = userService.update(UpdateUserDTO.builder()
		  .userId(id)
		  .newUsername(userUpdateRequest.getNewUsername())
		  .newEmail(userUpdateRequest.getNewEmail())
		  .newPassword(userUpdateRequest.getNewPassword())
		  .newProfilePicture(biContentDTO.orElse(null))
		  .build());
		log.debug("user updated Request successfully done. newUsername={}", userUpdateRequest.getNewUsername());

		return ResponseEntity.ok(userMapper.toResponse(result));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
		log.debug("Request to delete user started userID={}", id);

		userService.delete(id);
		log.debug("user delete Request successfully done userID={}", id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{userId}/userStatus")
	public ResponseEntity<UserStatusResponse> updateUserStatus(
	  @PathVariable UUID userId,
	  @RequestBody UpdateUserStatusRequest updateUserStatusRequest) {

		UserStatusDto newUserStatus = userStatusService.updateStatusByUserId(
		  UpdateStatusByUserIdDTO.builder()
			.userId(userId)
			.newLastActiveAt(updateUserStatusRequest.getNewLastActiveAt())
			.build());

		return ResponseEntity.ok(userStatusMapper.toResponse(newUserStatus));
	}

}
