package com.sprint.mission.discodeit.service.basic;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.dto.user.JwtInformation;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
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
	private final JwtRegistry<UUID> jwtRegistry;
	private final JwtTokenProvider tokenProvider;
	private final UserDetailsService userDetailsService;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public UserDto updateRole(UserRoleUpdateRequest request) {
		log.debug("[AuthService#updateRole] request={}]", request);
		User user = userRepository.findById(request.userId())
			.orElseThrow(() -> new UserNotFoundException().addDetail("userId", request.userId()));
		String oldRole = user.getRole().name();
		user.update(null, null, null, null, request.newRole());
		UserDto dto = userMapper.toDto(userRepository.save(user));

		jwtRegistry.invalidateJwtInformationByUserId(user.getId());
		eventPublisher.publishEvent(new RoleUpdatedEvent(oldRole, request.newRole().name(), user.getId()));
		log.info("[AuthService#updateRole] success dto={}", dto);
		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	public JwtInformation refreshToken(String refreshToken) {
		if (!tokenProvider.validateRefreshToken(refreshToken)
			|| !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
			log.info("Invalid or expired refresh token: {}", refreshToken);
			throw new DiscodeitException(ErrorCode.INVALID_TOKEN);
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

			jwtRegistry.rotateJwtInformation(refreshToken, newJwtInformation);
			return newJwtInformation;
		} catch (Exception e) {
			throw new DiscodeitException(ErrorCode.INTERNAL_SERVER_ERROR, e);
		}
	}
}
