package com.sprint.mission.discodeit.security;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userStatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;
	private final UserStatusRepository userStatusRepository;
	private final BinaryContentMapper binaryContentMapper;

	@Override
	@Transactional(readOnly = true)
	public DiscodeitUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepository.findByUsername(username)
		  .orElseThrow(() -> new UserNotFoundException(Map.of("username", username)));

		UUID userId = user.getId();
		String password = user.getPassword();
		BinaryContentDto profileImage = binaryContentMapper.toDto(user.getProfileImage());
		boolean isOnline = userStatusRepository.findByUserId(userId)
		  .map(UserStatus::isOnline)
		  .orElseThrow(UserStatusNotFoundException::new);

		UserDto userDto = UserDto.of(user, profileImage, isOnline);

		return DiscodeitUserDetails.from(userDto, password);

	}
}
