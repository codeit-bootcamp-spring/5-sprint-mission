package com.sprint.mission.discodeit.service.basic;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.user.JwtInformation;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.SessionManager;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final SessionManager sessionManager;
	private final JwtRegistry<UUID> jwtRegistry;
	private final JwtTokenProvider tokenProvider;
	private final UserDetailsService userDetailsService;

	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public UserDto updateRole(UserRoleUpdateRequest request) {
		log.debug("[AuthService#updateRole] request={}]", request);
		User user = userRepository.findById(request.userId())
			.orElseThrow(() -> new UserNotFoundException().addDetail("userId", request.userId()));
		user.update(null, null, null, null, request.newRole());
		UserDto dto = userMapper.toDto(userRepository.save(user));

		sessionManager.invalidateSessionsByUserId(user.getId());

		log.info("[AuthService#updateRole] success dto={}", dto);
		return dto;
	}

	@Override
	public JwtInformation refreshToken(String refreshToken) {
		if (!tokenProvider.validateRefreshToken(refreshToken)
			|| !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
			log.info("Invalid or expired refresh token: {}", refreshToken);
			throw new RuntimeException("Invalid or expired refresh token"); // TODO: JWT 관련 예외 추가
		}
		String username = tokenProvider.getUsernameFromToken(refreshToken);
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		if (userDetails == null) {
			throw new UserNotFoundException().addDetail("username", username);
		}

		try {
			DiscodeitUserDetails discodeitUserDetails = (DiscodeitUserDetails)userDetails;
			String newAccessToken = tokenProvider.generateAccessToken(discodeitUserDetails);
			String newRefreshToken = tokenProvider.generateRefreshToken(discodeitUserDetails);

			JwtInformation newJwtInformation = new JwtInformation(
				discodeitUserDetails.getUserDto(),
				newAccessToken,
				newRefreshToken
			);

			jwtRegistry.rotateJwtInformation(newRefreshToken, newJwtInformation);
			return newJwtInformation;
		} catch (Exception e) {
			throw new RuntimeException(e); // TODO:INTERNAL_SERVER_ERROR 예외 추가
		}
	}
}
