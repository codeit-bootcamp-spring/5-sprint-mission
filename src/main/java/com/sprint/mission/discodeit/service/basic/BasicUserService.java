package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DuplicateLoginIdException;
import com.sprint.mission.discodeit.exception.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

public class BasicUserService implements UserService {
	private final UserRepository userRepository;

	public BasicUserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public User login(String loginId, String password) {
		User user = userRepository.findByLoginId(loginId);
		if (user == null) {
			throw new UserNotFoundException();
		}

		if (!user.getPassword().equals(password)) {
			throw new InvalidPasswordException();
		}

		return user;
	}

	@Override
	public User createUser(String loginId, String password, String defaultNickname) {
		if (userRepository.existsByLoginId(loginId)) {
			throw new DuplicateLoginIdException();
		}

		User newUser = new User(loginId, password, defaultNickname);
		userRepository.save(newUser);

		return newUser;
	}

	@Override
	public User getUserById(UUID id) {
		User user= userRepository.findById(id);

		if (user == null) {
			throw new UserNotFoundException();
		}

		return user;
	}

	@Override
	public User getUserByLoginId(String loginId) {
		User user = userRepository.findByLoginId(loginId);

		if (user == null) {
			throw new UserNotFoundException();
		}

		return user;
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public boolean updateUserPassword(UUID id, String password) {
		User user = getUserById(id);

		user.updatePassword(password);

		userRepository.save(user);

		// 만약 비밀번호 설정 제약이 있다면 받은 비밀번호 검사 후 boolean타입으로 반환
		return true;
	}

	@Override
	public boolean deleteUser(UUID id) {
		User user = getUserById(id);

		userRepository.deleteById(id);

		return true;
	}

	@Override
	public boolean deleteUser(String LoginId) {
		User user = getUserByLoginId(LoginId);

		userRepository.deleteByLoginId(LoginId);

		return true;
	}
}
