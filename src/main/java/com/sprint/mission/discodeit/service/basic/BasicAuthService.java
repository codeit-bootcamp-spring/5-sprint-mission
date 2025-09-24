package com.sprint.mission.discodeit.service.basic;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.LoginParams;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.exception.auth.WrongPasswordException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userStatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final UserStatusRepository userStatusRepository;
	private final BinaryContentMapper binaryContentMapper;

	@Override
	@Transactional(readOnly = true)
	public UserDto login(LoginParams params) {
		User user = userRepository.findByUsernameWithProfileImage(params.getUsername())
		  .orElseThrow(() -> new UserNotFoundException(Map.of("username", params.getUsername())));
		if (!user.getPassword().equals(params.getPassword())) {
			throw new WrongPasswordException();
		}

		UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
		  .orElseThrow(UserStatusNotFoundException::new);

		return userMapper.toDto(user, userStatus.isOnline(), binaryContentMapper.toDto(user.getProfileImage()));
	}

}
