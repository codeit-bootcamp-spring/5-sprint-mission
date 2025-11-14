package com.sprint.mission.discodeit.service.basic;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.domain.dto.command.UpdateRoleCommand;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.enums.Role;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userStatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

	private final UserRepository userRepository;
	private final UserStatusRepository userStatusRepository;

	@Override
	@Transactional
	@PreAuthorize("hasAuthority('ADMIN')")
	public UserDto updateRole(UpdateRoleCommand command) {
		UUID userId = command.getUserId();
		Role role = command.getRole();

		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		user.setRole(role);

		UserStatus userStatus = userStatusRepository
		  .findByUserId(userId).orElseThrow(UserStatusNotFoundException::new);

		boolean isOnline = userStatus.isOnline();
		BinaryContentDto profile = Optional.ofNullable(user.getProfileImage())
		  .map(BinaryContentDto::of)
		  .orElse(null);

		return UserDto.of(user, profile, isOnline);
	}
}
