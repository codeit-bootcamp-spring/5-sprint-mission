package com.sprint.mission.discodeit.service.basic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.request.user.*;
import com.sprint.mission.discodeit.dto.response.user.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.DuplicateLoginIdException;
import com.sprint.mission.discodeit.exception.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
	private final UserRepository userRepository;
	private final UserStatusRepository userStatusRepository;
	private final BinaryContentRepository binaryContentRepository;

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

		UserStatus userStatus = new UserStatus(user.getId());
		userStatusRepository.save(userStatus);

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

		// 만약 비밀번호 설정 제약이 있다면 받은 비밀번호 검사 후 Response 반환
		return new UpdateUserPasswordResponse(true);
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
}
