package com.sprint.mission.discodeit.security;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionManager {
	private final SessionRegistry sessionRegistry;

	// userId로 저장된 세션들(여러 세션이 존재 가능)을 찾아오는 메서드
	public List<SessionInformation> getActiveSessionsByUserId(UUID userId) {
		return sessionRegistry.getAllPrincipals().stream()
			.filter(principal -> principal instanceof DiscodeitUserDetails)
			.map(DiscodeitUserDetails.class::cast)
			.filter(details -> details.getUserDto() != null && userId.equals(details.getUserDto().id()))
			.flatMap(details -> sessionRegistry.getAllSessions(details, false).stream())
			.toList();
	}

	// 세션을 무효화 하는 기능
	public void invalidateSessionsByUserId(UUID userId) {
		List<SessionInformation> activeSessions = getActiveSessionsByUserId(userId);
		if (!activeSessions.isEmpty()) {
			activeSessions.forEach(SessionInformation::expireNow); // 세션 무효화 코드
			log.info("Session invalidate size : {}", activeSessions.size());
		}
	}
}
