package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.domain.dto.command.GetNewAccTokenCommand;
import com.sprint.mission.discodeit.domain.dto.command.UpdateRoleCommand;
import com.sprint.mission.discodeit.domain.dto.jwt.JwtDto;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.enums.Role;
import com.sprint.mission.discodeit.exception.auth.NotAuthenticationException;
import com.sprint.mission.discodeit.exception.server.InternalServerErrorException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.security.SecurityService;
import com.sprint.mission.discodeit.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

	private final UserRepository userRepository;
	private final SessionRegistry sessionRegistry;
	private final SecurityService securityService;
	private final JwtTokenProvider jwtTokenProvider;
	private final DiscodeitUserDetailsService discodeitUserDetailsService;

	@Override
	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public UserDto updateRole(UpdateRoleCommand command) {
		UUID userId = command.getUserId();
		Role role = command.getRole();

		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		user.setRole(role);

		boolean isOnline = securityService.isOnline(user);
		BinaryContentDto profile = Optional.ofNullable(user.getProfileImage())
		  .map(BinaryContentDto::of)
		  .orElse(null);

		registerExpireSessionAfterCommit(user);

		return UserDto.of(user, profile, isOnline);
	}

	@Override
	public JwtDto getNewAccToken(GetNewAccTokenCommand command) {
		String refreshToken = command.refreshToken();
		HttpServletResponse response = command.response();

		// 유효하지 않다면 401
		if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
			throw new NotAuthenticationException();
		}

		String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
		DiscodeitUserDetails userDetails = discodeitUserDetailsService.loadUserByUsername(username);

		try {
			String newAccToken = jwtTokenProvider.generateAccessToken(userDetails);
			String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
			Cookie newAccTokenCookie = jwtTokenProvider.genereateRefreshTokenCookie(newRefreshToken);

			response.addCookie(newAccTokenCookie);
			return new JwtDto(userDetails.getUserDto(), newAccToken);

		} catch (JOSEException e) {
			throw new InternalServerErrorException(Map
			  .of("auth service", "generateAccessToken 실패"));
		}

	}

	@Override
	public UserDto getProfile(String refreshToken) {
		if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
			throw new NotAuthenticationException();
		}
		String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
		DiscodeitUserDetails userDetails = discodeitUserDetailsService.loadUserByUsername(username);

		return userDetails.getUserDto();
	}

	private void registerExpireSessionAfterCommit(User user) {
		TransactionSynchronizationManager.registerSynchronization(
		  new TransactionSynchronization() {
			  @Override
			  public void afterCommit() {
				  expireUserSessions(user);
				  System.out.println("afterCommit");
			  }
		  }
		);
	}

	private void expireUserSessions(User user) {
		String username = user.getUsername();

		List<DiscodeitUserDetails> targetUserDetails = sessionRegistry.getAllPrincipals().stream()
		  .filter(principal -> principal instanceof DiscodeitUserDetails) // 타입 체크
		  .map(principal -> (DiscodeitUserDetails)principal)             // 캐스팅
		  .filter(details -> details.getUsername().equals(username))     // username 필터
		  .toList();

		List<SessionInformation> sessions = targetUserDetails.stream()
		  .map(details -> sessionRegistry.getAllSessions(details, false)) // List<SessionInformation>
		  .flatMap(List::stream)
		  .toList();

		sessions.forEach(SessionInformation::expireNow);

	}
}
