package com.sprint.mission.discodeit.service.basic;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;

	@Override
	@Transactional
	public UserDto updateRole(UUID userId, Role role) {
		User user = userRepository.findById(userId).orElseThrow();
		user.setRole(role);
		return userMapper.toDto(user);
	}
}
