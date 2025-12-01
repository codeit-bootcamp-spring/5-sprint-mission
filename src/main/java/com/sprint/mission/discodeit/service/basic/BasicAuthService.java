package com.sprint.mission.discodeit.service.basic;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.Auth.InvalidTokenException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.dto.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.dto.JwtDto;
import com.sprint.mission.discodeit.security.dto.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtSession;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.registry.JwtRegistry;
import com.sprint.mission.discodeit.security.service.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.session.SessionManager;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final SessionManager sessionManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final JwtRegistry<UUID> jwtRegistry;
	private final DiscodeitUserDetailsService userDetailsService;

	@PreAuthorize("hasRole('ADMIN')")
	@Override
	@Transactional
	public UserDto updateRole(UUID userId, Role role) {
		return updateLogic(userId, role);
	}

	@Override
	@Transactional
	public UserDto updateLogic(UUID userId, Role role) {
		User user = userRepository.findById(userId).orElseThrow();
		user.setRole(role);
		sessionManager.invalidateSessionByUserId(userId);
		return userMapper.toDto(user);
	}

	@Override
	public JwtDto reGenerateToken(String refreshToken) {
		if (!jwtTokenProvider.validateRefreshToken(refreshToken) || !jwtRegistry.hasActiveJwtInformationByRefreshToken(
			refreshToken)) {
			throw InvalidTokenException.withRefreshToken("Invalid refresh token");
		}

		String username = jwtTokenProvider.getUsername(refreshToken);
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		if (userDetails == null) {
			throw UserNotFoundException.withUsername("Invalid username or password");
		}

		try {
			DiscodeitUserDetails discodeitUserDetails = (DiscodeitUserDetails)userDetails;
			JwtSession jwtSession = jwtTokenProvider.generateTokens(discodeitUserDetails);
			String newAccessToken = jwtSession.getAccessToken();
			String newRefreshToken = jwtSession.getRefreshToken();
			JwtInformation newJwtInfo = JwtInformation.builder()
				.userDto(discodeitUserDetails.getUserDto())
				.accessToken(newAccessToken)
				.refreshToken(newRefreshToken)
				.build();
			jwtRegistry.rotateJwtInformation(refreshToken, newJwtInfo);

			return new JwtDto(newAccessToken, discodeitUserDetails.getUserDto());
		} catch (JOSEException e) {
			throw new RuntimeException("Error generating access token", e);
		}
	}
}
