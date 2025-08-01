package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DuplicateLoginIdException;
import com.sprint.mission.discodeit.exception.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
public class BasicUserService implements UserService {
	private final UserRepository userRepository;

	public BasicUserService(@Qualifier("fileUserRepository") UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public User login(String loginId, String password) {
		User user = userRepository.findByLoginId(loginId)
			.orElseThrow(UserNotFoundException::new);

		if (!user.getPassword().equals(password)) {
			throw new InvalidPasswordException();
		}

		return user;
	}

	@Override
	public User createUser(UserCreateRequest request) {
		if (userRepository.existsByLoginId(request.getLoginId())) {
			throw new DuplicateLoginIdException();
		}

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new DuplicateLoginIdException();
		}

		User user = request.toUser();
		userRepository.save(user);

		return getUserById(user.getId());
	}

	@Override
	public User getUserById(UUID id) {
		return userRepository.findById(id)
			.orElseThrow(UserNotFoundException::new);
	}

	@Override
	public User getUserByLoginId(String loginId) {
		return userRepository.findByLoginId(loginId)
			.orElseThrow(UserNotFoundException::new);
	}


	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public boolean updateUserPassword(UUID id, String password) {
		User user = userRepository.findById(id)
			.orElseThrow(UserNotFoundException::new);

		user.updatePassword(password);

		userRepository.save(user);

		// 만약 비밀번호 설정 제약이 있다면 받은 비밀번호 검사 후 boolean타입으로 반환
		return true;
	}

	@Override
	public boolean deleteUser(UUID id) {
		userRepository.findById(id)
			.orElseThrow(UserNotFoundException::new);

		userRepository.deleteById(id);

		return true;
	}

	@Override
	public boolean deleteUser(String LoginId) {
		userRepository.findByLoginId(LoginId)
			.orElseThrow(UserNotFoundException::new);

		userRepository.deleteByLoginId(LoginId);

		return true;
	}
}
