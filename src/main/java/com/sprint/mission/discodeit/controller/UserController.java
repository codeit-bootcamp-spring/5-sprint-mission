package com.sprint.mission.discodeit.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sprint.mission.discodeit.dto.request.binaryContent.CreateUserProfileImageRequest;
import com.sprint.mission.discodeit.dto.request.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.user.GetUserByIdRequest;
import com.sprint.mission.discodeit.dto.request.user.UpdateUserDefalutNicknameRequest;
import com.sprint.mission.discodeit.dto.response.user.DeleteUserResponse;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final UserStatusService userStatusService;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<UserResponse>> getUserAll() {

		List<UserResponse> userResponses = userService.getAllUsers();

		return ResponseEntity.ok(userResponses);
	}

	// username(=loginId)로 조회
	@RequestMapping(path = "/{username}", method = RequestMethod.GET)
	public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
		UserResponse userResponse = userService.getUserByLoginId(username);
		boolean online = userStatusService.isOnline(userResponse.getId());
		userResponse.setOnline(online);
		return ResponseEntity.ok(userResponse);
	}

	@RequestMapping(path = "/id/{id}", method = RequestMethod.GET)
	public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
		GetUserByIdRequest request = GetUserByIdRequest.builder()
				.id(id)
				.build();
		UserResponse userResponse = userService.getUserById(request);
		boolean online = userStatusService.isOnline(id);
		userResponse.setOnline(online);

		return ResponseEntity.ok(userResponse);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UserResponse> createUser(
		@RequestPart("user") CreateUserRequest request,
		@RequestPart(value = "profileImage", required = false) MultipartFile profileImage
	) {
		try {
			if (profileImage != null && !profileImage.isEmpty()) {
				CreateUserProfileImageRequest imageRequest = CreateUserProfileImageRequest.builder()
					.filename(profileImage.getOriginalFilename())
					.contentType(profileImage.getContentType())
					.size(profileImage.getSize())
					.content(profileImage.getBytes())
					.build();
				request.setProfileImage(imageRequest);
			}

			UserResponse response = userService.createUser(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IOException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.PATCH)
	public ResponseEntity<UserResponse> updateUserDefalutNickname(UpdateUserDefalutNicknameRequest request) {
		UserResponse userResponse = userService.updateUserDefalutNickname(request);

		return ResponseEntity.ok(userResponse);
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<DeleteUserResponse> deleteUserById(@PathVariable UUID id) {
		DeleteUserResponse deleteUserResponse = userService.delete(id);
		return ResponseEntity.ok(deleteUserResponse);
	}

	@RequestMapping(path = "/{id}/status", method = RequestMethod.GET)
	public ResponseEntity<Boolean> getUserStatusById(@PathVariable UUID id) {
		userStatusService.updateByUserId(id);
		boolean online = userStatusService.isOnline(id);
		return ResponseEntity.ok(online);
	}
}
