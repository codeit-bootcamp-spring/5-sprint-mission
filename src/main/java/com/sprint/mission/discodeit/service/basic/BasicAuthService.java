package com.sprint.mission.discodeit.service.basic;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.InvalidCredentialsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service("authService")
@RequiredArgsConstructor
@Validated
public class BasicAuthService implements AuthService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;

	@Override
	@Transactional
	public UserDto login(LoginRequest loginRequest) {
		User user = userRepository.findByUsername(loginRequest.username())
			.orElseThrow(
				() -> new UserNotFoundException().addDetail("username", loginRequest.username()));

		if (!user.getPassword().equals(loginRequest.password())) {
			throw new InvalidCredentialsException();
		}

		return userMapper.toDto(user);
	}
}
