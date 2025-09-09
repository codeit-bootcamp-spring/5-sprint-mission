package com.sprint.mission.discodeit.service.basic;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.LoginParams;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
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
		  .orElseThrow(() -> new IllegalArgumentException("User with username " + params.getUsername() + " not found"));
		if (!user.getPassword().equals(params.getPassword())) {
			throw new IllegalArgumentException("Wrong password");
		}

		UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElseThrow(() ->
		  new NoSuchElementException("userStatus With userID" + user.getId() + "not found")
		);

		return userMapper.toDto(user, userStatus.isOnline(), binaryContentMapper.toDto(user.getProfileImage()));
	}

}
