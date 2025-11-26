package com.sprint.mission.discodeit.security;

import java.util.Map;

import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;
	private final BinaryContentMapper binaryContentMapper;
	private final SessionRegistry sessionRegistry;

	@Override
	@Transactional(readOnly = true)
	public DiscodeitUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepository.findByUsername(username)
		  .orElseThrow(() -> new UserNotFoundException(Map.of("username", username)));

		String password = user.getPassword();
		BinaryContentDto profileImage = binaryContentMapper.toDto(user.getProfileImage());
		boolean isOnline = isOnline(user);

		UserDto userDto = UserDto.of(user, profileImage, isOnline);

		return DiscodeitUserDetails.from(userDto, password);

	}

	private boolean isOnline(User user) {
		final long ONLINE_THRESHOLD_MS = 5 * 60 * 1000L; // 5분(5 * 60초 * 1000ms)
		long now = System.currentTimeMillis();

		return sessionRegistry.getAllPrincipals().stream()
		  .filter(principal -> principal instanceof DiscodeitUserDetails)
		  .map(principal -> (DiscodeitUserDetails)principal)
		  .filter(details -> details.getUsername().equals(user.getUsername()))
		  .flatMap(details -> sessionRegistry.getAllSessions(details, false).stream())
		  .anyMatch(sessionInfo -> {
			  long lastRequest = sessionInfo.getLastRequest().getTime();
			  return (now - lastRequest) <= ONLINE_THRESHOLD_MS;
		  });
	}
}
