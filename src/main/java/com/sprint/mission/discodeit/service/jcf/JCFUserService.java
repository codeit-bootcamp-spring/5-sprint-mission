package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.sprint.mission.discodeit.dto.request.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.user.DeleteUserByIdRequest;
import com.sprint.mission.discodeit.dto.request.user.DeleteUserByLoingIdRequest;
import com.sprint.mission.discodeit.dto.request.user.GetUserByIdRequest;
import com.sprint.mission.discodeit.dto.request.user.GetUserByLoginIdRequest;
import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.request.user.UpdateUserPasswordRequest;
import com.sprint.mission.discodeit.dto.response.user.CreateUserResponse;
import com.sprint.mission.discodeit.dto.response.user.DeleteUserResponse;
import com.sprint.mission.discodeit.dto.response.user.GetUserResponse;
import com.sprint.mission.discodeit.dto.response.user.LoginResponse;
import com.sprint.mission.discodeit.dto.response.user.UpdateUserPasswordResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

public class JCFUserService implements UserService {
	private final Map<UUID, User> UserMap;
	private final Map<String, UUID> loginIdToUUID;

	public JCFUserService() {
		UserMap = new ConcurrentHashMap<>();
		loginIdToUUID = new ConcurrentHashMap<>();
	}

	@Override
	public CreateUserResponse createUser(CreateUserRequest request) {

		if (request.getLoginId() == null)
			return null;
		if (isExistLoginId(request.getLoginId()))
			return null;

		User user = new User(request.toUser());
		UserMap.put(user.getId(), user);
		loginIdToUUID.put(user.getLoginId(), user.getId());

		return CreateUserResponse.success(user);
	}


	public LoginResponse login(LoginRequest request) {
		if (request.getLoginId() == null || request.getPassword() == null)
			return null;
		if (loginIdToUUID.containsKey(request.getLoginId())) {
			User user = UserMap.get(loginIdToUUID.get(request.getLoginId()));
			if (user.getPassword().equals(request.getPassword())) {
				return LoginResponse.success(user);
			} else {
				return LoginResponse.success(null);
			}
		}
		return LoginResponse.success(null);
	}

	@Override
	public GetUserResponse getUserById(GetUserByIdRequest request) {
		User user = UserMap.get(request.getId());
		return GetUserResponse.success(user);
	}

	@Override
	public GetUserResponse getUserByLoginId(GetUserByLoginIdRequest request) {
		User user = UserMap.get(loginIdToUUID.get(request.getLoginId()));
		return GetUserResponse.success(user);
	}

	@Override
	public List<GetUserResponse> getAllUsers() {
		List<User> userList = new ArrayList<>(UserMap.values());
		userList.sort((u1, u2) -> u1.getDefaultNickname().compareTo(u2.getDefaultNickname()));

		return userList.stream()
			.map(GetUserResponse::success)
			.toList();
	}

	@Override
	public UpdateUserPasswordResponse updateUserPassword(UpdateUserPasswordRequest request) {
		if (request.getId() == null || request.getCurrentPassword() == null)
			return new UpdateUserPasswordResponse(false);
		UserMap.get(request.getId()).updatePassword(request.getNewPassword());

		return new UpdateUserPasswordResponse(true);
	}

	@Override
	public DeleteUserResponse deleteUser(DeleteUserByIdRequest request) {
		if (request.getId() == null || !UserMap.containsKey(request.getId())) {
			return new DeleteUserResponse(false);
		}

		loginIdToUUID.remove(UserMap.get(request.getId()).getLoginId());
		UserMap.remove(request.getId());

		return new DeleteUserResponse(true);
	}

	@Override
	public DeleteUserResponse deleteUser(DeleteUserByLoingIdRequest request) {
		if (request.getLoginId() == null || !loginIdToUUID.containsKey(request.getLoginId())) {
			return new DeleteUserResponse(false);
		}

		UserMap.remove(loginIdToUUID.get(request.getLoginId()));
		loginIdToUUID.remove(request.getLoginId());

		return new DeleteUserResponse(true);
	}

	public boolean isExistLoginId(String loginId) {
		return loginIdToUUID.containsKey(loginId);
	}
}
