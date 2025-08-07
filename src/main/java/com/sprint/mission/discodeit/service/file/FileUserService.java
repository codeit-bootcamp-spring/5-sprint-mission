package com.sprint.mission.discodeit.service.file;

import java.util.List;
import java.util.Optional;

import com.sprint.mission.discodeit.dto.request.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.user.DeleteUserByIdRequest;
import com.sprint.mission.discodeit.dto.request.user.DeleteUserByLoingIdRequest;
import com.sprint.mission.discodeit.dto.request.user.GetUserByIdRequest;
import com.sprint.mission.discodeit.dto.request.user.GetUserByLoginIdRequest;
import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.request.user.UpdateUserPasswordRequest;
import com.sprint.mission.discodeit.dto.request.user.UpdateUserProfileImageRequest;
import com.sprint.mission.discodeit.dto.response.user.CreateUserResponse;
import com.sprint.mission.discodeit.dto.response.user.DeleteUserResponse;
import com.sprint.mission.discodeit.dto.response.user.GetUserResponse;
import com.sprint.mission.discodeit.dto.response.auth.LoginResponse;
import com.sprint.mission.discodeit.dto.response.user.UpdateUserPasswordResponse;
import com.sprint.mission.discodeit.dto.response.user.UpdateUserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.DuplicateLoginIdException;
import com.sprint.mission.discodeit.exception.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

public class FileUserService implements UserService {

	private final UserRepository userRepository;

	public FileUserService(UserRepository userRepository) {
		this.userRepository =userRepository;
	}


	public LoginResponse login(LoginRequest request) {
		Optional<User> userOpt = userRepository.findByLoginId(request.getLoginId());
		if (userOpt.isPresent()) {
			User user = userOpt.get();
			if (user.getPassword().equals(request.getPassword())) {
				return LoginResponse.success(user);
			}
		}
		return null;
	}

	@Override
	public CreateUserResponse createUser(CreateUserRequest request) {
		if (userRepository.existsByLoginId(request.getLoginId())) {
			throw new DuplicateLoginIdException();
		}

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new DuplicateEmailException();
		}

		User user = request.toUser();
		userRepository.save(user);

		return CreateUserResponse.success(user);
	}

	@Override
	public GetUserResponse getUserById(GetUserByIdRequest request) {
		User user = userRepository.findById(request.getId())
			.orElseThrow(UserNotFoundException::new);

		return GetUserResponse.success(user);
	}

	@Override
	public GetUserResponse getUserByLoginId(GetUserByLoginIdRequest request) {
		User user = userRepository.findByLoginId(request.getLoginId())
			.orElseThrow(UserNotFoundException::new);

		return GetUserResponse.success(user);
	}

	@Override
	public List<GetUserResponse> getAllUsers() {
		return userRepository.findAll().stream()
			.map(GetUserResponse::success)
			.toList();
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
	public UpdateUserResponse updateUserProfile(UpdateUserProfileImageRequest request) {
		return null;
	}

	@Override
	public DeleteUserResponse deleteUser(DeleteUserByIdRequest request) {
		if (!userRepository.existsById(request.getId())) {
			throw new UserNotFoundException();
		}

		userRepository.deleteById(request.getId());

		return new DeleteUserResponse(true);
	}

	@Override
	public DeleteUserResponse deleteUser(DeleteUserByLoingIdRequest request) {
		if (!userRepository.existsByLoginId(request.getLoginId())) {
			throw new UserNotFoundException();
		}

		userRepository.deleteByLoginId(request.getLoginId());

		return new DeleteUserResponse(true);
	}

	public boolean existsLoginId(String loginId) {
		return userRepository.existsByLoginId(loginId);
	}
}
