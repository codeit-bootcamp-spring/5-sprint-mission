package com.sprint.mission.discodeit.service.file;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.user.DeleteUserByIdRequest;
import com.sprint.mission.discodeit.dto.request.user.DeleteUserByLoingIdRequest;
import com.sprint.mission.discodeit.dto.request.user.GetUserByIdRequest;
import com.sprint.mission.discodeit.dto.request.user.GetUserByLoginIdRequest;
import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.request.user.UpdateUserDefalutNicknameRequest;
import com.sprint.mission.discodeit.dto.request.user.UpdateUserPasswordRequest;
import com.sprint.mission.discodeit.dto.request.user.UpdateUserProfileImageRequest;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.dto.response.user.DeleteUserResponse;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.dto.response.auth.LoginResponse;
import com.sprint.mission.discodeit.dto.response.user.UpdateUserPasswordResponse;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.DuplicateLoginIdException;
import com.sprint.mission.discodeit.exception.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;

public class FileUserService implements UserService {
	private final UserStatusRepository userStatusRepository;
	private final UserRepository userRepository;

	public FileUserService(UserRepository userRepository, UserStatusRepository userStatusRepository) {
		this.userRepository =userRepository;
		this.userStatusRepository = userStatusRepository;
	}


	public LoginResponse login(LoginRequest request) {
		Optional<User> userOpt = userRepository.findByLoginId(request.getUsername());
		if (userOpt.isPresent()) {
			User user = userOpt.get();
			if (user.getPassword().equals(request.getPassword())) {
				return LoginResponse.success(user);
			}
		}
		return null;
	}

	@Override
	public UserResponse createUser(CreateUserRequest request) {
		if (userRepository.existsByLoginId(request.getUsername())) {
			throw new DuplicateLoginIdException();
		}

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new DuplicateEmailException();
		}

		User user = request.toUser();
		userRepository.save(user);

		return UserResponse.success(user);
	}

	@Override
	public UserResponse getUserById(GetUserByIdRequest request) {
		User user = userRepository.findById(request.getId())
			.orElseThrow(UserNotFoundException::new);

		return UserResponse.success(user);
	}

	@Override
	public UserResponse getUserByLoginId(String loginId) {
		User user = userRepository.findByLoginId(loginId)
			.orElseThrow(UserNotFoundException::new);


		return UserResponse.success(user);
	}

	@Override
	public List<UserResponse> getAllUsers() {
		List<User> users = userRepository.findAll();
		List<UserResponse> userResponseList = new ArrayList<>();

		for (User user : users) {
			userResponseList.add(UserResponse.success(user));
		}

		return userResponseList;
	}

	@Override
	public UpdateUserPasswordResponse updateUserPassword(UpdateUserPasswordRequest request) {
		User user = userRepository.findById(request.getId())
			.orElseThrow(UserNotFoundException::new);

		if (!user.getPassword().equals(request.getCurrentPassword())) {
			throw new InvalidPasswordException();
		}

		user.updatePassword(request.getNewPassword());

		userRepository.save(user);

		// 만약 비밀번호 설정 제약이 있다면 받은 비밀번호 검사 후 boolean타입으로 반환
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
		User user = userRepository.findById(id)
			.orElseThrow(UserNotFoundException::new);

		userRepository.deleteById(id);

		return DeleteUserResponse.success(user);
	}

	@Override
	public DeleteUserResponse delete(String loginId) {
		User user = userRepository.findByLoginId(loginId)
			.orElseThrow(UserNotFoundException::new);

		userRepository.deleteByLoginId(loginId);

		return DeleteUserResponse.success(user);
	}

	public boolean existsLoginId(String loginId) {
		return userRepository.existsByLoginId(loginId);
	}
}
