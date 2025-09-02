package com.sprint.mission.discodeit.service.basic;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.response.auth.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
	private final UserRepository userRepository;

	@Override
    @Transactional(readOnly = true)
	public LoginResponse login(LoginRequest request) {
		User user = userRepository.findByUsername(request.getUsername())
			.orElseThrow(UserNotFoundException::new);

		if (user.getPassword() == null || !user.getPassword().equals(request.getPassword())) {
			throw new InvalidPasswordException();
		}

		return LoginResponse.success(user);
	}
}
