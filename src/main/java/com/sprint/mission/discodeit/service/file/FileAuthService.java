package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.response.auth.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;

public class FileAuthService implements AuthService {
	private final UserRepository userRepository;

	public FileAuthService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public LoginResponse login(LoginRequest request) {
		User user = userRepository.findByLoginId(request.getUsername())
			.orElseThrow(UserNotFoundException::new);

		if (user.getPassword() == null || !user.getPassword().equals(request.getPassword())) {
			throw new InvalidPasswordException();
		}

		return LoginResponse.success(user);
	}
}