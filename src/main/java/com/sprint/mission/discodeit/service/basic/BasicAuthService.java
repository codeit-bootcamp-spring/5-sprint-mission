package com.sprint.mission.discodeit.service.basic;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
	private final UserRepository userRepository;
	private final UserMapper userMapper;

	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public UserDto updateRole(UserRoleUpdateRequest request) {
		log.debug("[AuthService#updateRole] request={}]", request);
		User user = userRepository.findById(request.userId())
			.orElseThrow(() -> new UserNotFoundException().addDetail("userId", request.userId()));
		user.update(null, null, null, null, request.newRole());
		UserDto dto = userMapper.toDto(userRepository.save(user));

		log.info("[AuthService#updateRole] success dto={}", dto);
		return dto;
	}
}
