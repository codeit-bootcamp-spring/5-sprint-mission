package com.sprint.mission.discodeit.controller;

import java.util.List;
import java.util.UUID;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.dto.request.user.CreateUserRequest;
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
		return ResponseEntity.ok(userResponse);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<UserResponse> createUser(CreateUserRequest request) {
		UserResponse userResponse = userService.createUser(request);
		return ResponseEntity.status(201).body(userResponse);
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


}
