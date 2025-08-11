package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.request.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.user.DeleteUserByIdRequest;
import com.sprint.mission.discodeit.dto.request.user.DeleteUserByLoingIdRequest;
import com.sprint.mission.discodeit.dto.request.user.GetUserByIdRequest;
import com.sprint.mission.discodeit.dto.request.user.UpdateUserDefalutNicknameRequest;
import com.sprint.mission.discodeit.dto.request.user.UpdateUserPasswordRequest;
import com.sprint.mission.discodeit.dto.request.user.UpdateUserProfileImageRequest;
import com.sprint.mission.discodeit.dto.response.auth.LoginResponse;
import com.sprint.mission.discodeit.dto.response.user.DeleteUserResponse;
import com.sprint.mission.discodeit.dto.response.user.UpdateUserPasswordResponse;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;

public class JCFUserService implements UserService {
	private final Map<UUID, User> UserMap;
	private final Map<String, UUID> loginIdToUUID;
	private UserStatusRepository userStatusRepository;

	public JCFUserService() {
		UserMap = new ConcurrentHashMap<>();
		loginIdToUUID = new ConcurrentHashMap<>();
	}

	@Override
	public UserResponse createUser(CreateUserRequest request) {

		if (request.getUsername() == null)
			return null;
		if (isExistLoginId(request.getUsername()))
			return null;

		User user = new User(request.toUser());
		UserMap.put(user.getId(), user);
		loginIdToUUID.put(user.getLoginId(), user.getId());

		return UserResponse.success(user);
	}


	public LoginResponse login(LoginRequest request) {
		if (request.getUsername() == null || request.getPassword() == null)
			return null;
		if (loginIdToUUID.containsKey(request.getUsername())) {
			User user = UserMap.get(loginIdToUUID.get(request.getUsername()));
			if (user.getPassword().equals(request.getPassword())) {
				return LoginResponse.success(user);
			} else {
				return LoginResponse.success(null);
			}
		}
		return LoginResponse.success(null);
	}

	@Override
	public UserResponse getUserById(GetUserByIdRequest request) {
		User user = UserMap.get(request.getId());
		return UserResponse.success(user);
	}

	@Override
	public UserResponse getUserByLoginId(String loginId) {
		User user = UserMap.get(loginIdToUUID.get(loginId));
		return UserResponse.success(user);
	}

	@Override
	public List<UserResponse> getAllUsers() {
		List<User> userList = new ArrayList<>(UserMap.values());
		userList.sort((u1, u2) -> u1.getDefaultNickname().compareTo(u2.getDefaultNickname()));
		List<UserResponse> userResponseList = new ArrayList<>();

		for (User user : userList) {
			userResponseList.add(UserResponse.success(user));
		}

		return userResponseList;
	}

	@Override
	public UpdateUserPasswordResponse updateUserPassword(UpdateUserPasswordRequest request) {
		if (request.getId() == null || request.getCurrentPassword() == null)
			return new UpdateUserPasswordResponse(false);
		UserMap.get(request.getId()).updatePassword(request.getNewPassword());

		return new UpdateUserPasswordResponse(true);
	}

	@Override
	public UserResponse updateUserDefalutNickname(UpdateUserDefalutNicknameRequest request) {
		return null;
	}

	@Override
	public UserResponse updateUserProfile(UpdateUserProfileImageRequest request) {
		return null;
	}

	@Override
	public DeleteUserResponse delete(UUID id) {
		if (id == null || !UserMap.containsKey(id)) {
			return null;
		}

		User user = UserMap.get(id);

		loginIdToUUID.remove(UserMap.get(id).getLoginId());
		UserMap.remove(id);

		return DeleteUserResponse.success(user);
	}

	@Override
	public DeleteUserResponse delete(String loginId) {
		if (loginId == null || !loginIdToUUID.containsKey(loginId)) {
			return null;
		}

		User user = UserMap.get(loginIdToUUID.get(loginId));

		UserMap.remove(loginIdToUUID.get(loginId));
		loginIdToUUID.remove(loginId);

		return DeleteUserResponse.success(user);
	}



	public boolean isExistLoginId(String loginId) {
		return loginIdToUUID.containsKey(loginId);
	}
}
