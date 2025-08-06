package com.sprint.mission.discodeit.service.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

public class FileUserService implements UserService {

	private final UserRepository userRepository;

	public FileUserService(UserRepository userRepository) {
		this.userRepository =userRepository;
	}

	@Override
	public User login(String loginId, String password) {
		Optional<User> userOpt = userRepository.findByLoginId(loginId);
		if (userOpt.isPresent()) {
			User user = userOpt.get();
			if (user.getPassword().equals(password)) {
				return user;
			}
		}
		return null;
	}

	@Override
	public User createUser(String loginId, String password, String defaultNickname) {
		if (userRepository.existsByLoginId(loginId)) return null;

		User user = new User(loginId, password, defaultNickname);

		userRepository.save(user);

		return user;
	}

	@Override
	public User getUserById(UUID id) {
		return userRepository.findById(id).orElse(null);
	}

	@Override
	public User getUserByLoginId(String loginId) {
		return userRepository.findByLoginId(loginId).orElse(null);
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public boolean updateUserPassword(UUID id, String password) {
		if (id == null || password == null) return false;

		Optional<User> userOpt = userRepository.findById(id);
		if (userOpt.isPresent()) {
			User user = userOpt.get();
			user.updatePassword(password);
			user.updateUpdatedAt();

			userRepository.save(user);
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteUser(UUID id) {
		if (id == null) return false;

		if (userRepository.findById(id).isPresent()) {
			userRepository.deleteById(id);
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteUser(String loginId) {
		if (loginId == null) return false;

		if (userRepository.findByLoginId(loginId).isPresent()) {
			userRepository.deleteByLoginId(loginId);
			return true;
		}
		return false;
	}

	public boolean existsLoginId(String loginId) {
		return userRepository.existsByLoginId(loginId);
	}
}
