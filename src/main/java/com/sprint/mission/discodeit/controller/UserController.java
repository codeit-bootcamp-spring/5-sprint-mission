package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.binaryContent.UserProfileImageRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateDefaultNicknameRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdatePasswordRequest;
import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserDeleteResponse;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final UserStatusService userStatusService;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<UserResponse>> getUserAll() {

		List<UserResponse> userResponses = userService.findAll();

		return ResponseEntity.ok(userResponses);
	}

	@RequestMapping(path = "/findAll", method = RequestMethod.GET)
	public ResponseEntity<List<UserDto>> findAllUsers() {
		List<UserResponse> userResponses = userService.findAll();

		List<UserDto> userDtos = userResponses.stream()
			.map(user -> new UserDto(
				user.getId(),
				user.getCreatedAt(),
				user.getUpdatedAt(),
				user.getNickname(), // 보여줄 username -> nickname
				user.getEmail(),
				user.getProfileId(),
				userStatusService.isOnline(user.getId())
			))
			.toList();

		return ResponseEntity.ok(userDtos);
	}

	// username(=loginId)로 조회
	@RequestMapping(path = "/username/{username}", method = RequestMethod.GET)
	public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
		UserResponse userResponse = userService.findByLoginId(username);
		boolean online = userStatusService.isOnline(userResponse.getId());
		userResponse.setOnline(online);
		return ResponseEntity.ok(userResponse);
	}

	@RequestMapping(path = "/{userId}", method = RequestMethod.GET)
	public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {
		UserResponse userResponse = userService.findById(userId);
		boolean online = userStatusService.isOnline(userId);
		userResponse.setOnline(online);

		return ResponseEntity.ok(userResponse);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UserResponse> createUser(
		@RequestPart("userCreateRequest") UserCreateRequest request,
		@RequestPart(value = "profile", required = false) MultipartFile profile
	) {
		try {
			if (profile != null && !profile.isEmpty()) {
				UserProfileImageRequest imageRequest = UserProfileImageRequest.builder()
					.fileName(profile.getOriginalFilename())
					.contentType(profile.getContentType())
					.size(profile.getSize())
					.bytes(profile.getBytes())
					.build();
				request.setProfileImage(imageRequest);
			}

			UserResponse response = userService.createUser(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IOException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@RequestMapping(path = "/{userId}/nickname", method = RequestMethod.PATCH)
	public ResponseEntity<UserResponse> updateUserNickname(
			@PathVariable UUID userId,
			@RequestBody UserUpdateDefaultNicknameRequest request) {
		UserResponse userResponse = userService.updateUserDefalutNickname(userId, request);
		return ResponseEntity.ok(userResponse);
	}

	@RequestMapping(path = "/{userId}/password", method = RequestMethod.PATCH)
	public ResponseEntity<UserResponse> updateUserPassword(
			@PathVariable UUID userId,
			@RequestBody UserUpdatePasswordRequest request) {
		UserResponse userResponse = userService.updateUserPassword(userId, request);
		return ResponseEntity.ok(userResponse);
	}

	@RequestMapping(path = "/{userId}/profile", method = RequestMethod.PATCH)
	public ResponseEntity<UserResponse> updateUserProfile(
			@PathVariable UUID userId,
			@RequestBody UserProfileImageRequest request) {
		UserResponse userResponse = userService.updateUserProfile(userId, request);
		return ResponseEntity.ok(userResponse);
	}

	@RequestMapping(path = "/{userId}", method = RequestMethod.DELETE)
	public ResponseEntity<UserDeleteResponse> deleteUserById(@PathVariable UUID userId) {
		UserDeleteResponse userDeleteResponse = userService.delete(userId);
		return ResponseEntity.ok(userDeleteResponse);
	}

	@RequestMapping(path = "/{userId}/userStatus", method = RequestMethod.PATCH)
	public ResponseEntity<Boolean> getUserStatusById(
			@PathVariable UUID userId,
			@RequestBody UserStatusUpdateRequest request) {
		userStatusService.update(userId, request);
		boolean online = userStatusService.isOnline(userId);
		return ResponseEntity.ok(online);
	}

	@RequestMapping(path = "/list", method = RequestMethod.GET)
	public ModelAndView userListPage() {
		return new ModelAndView("redirect:/user-list.html");
	}
}
