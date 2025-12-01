package com.sprint.mission.discodeit.security.session;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.security.dto.DiscodeitUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionManager {

	private final SessionRegistry sessionRegistry;

	public List<SessionInformation> getSessionsByUserId(UUID userId) {
		List<Object> allPrincipals = sessionRegistry.getAllPrincipals();

		List<SessionInformation> sessions = new ArrayList<>();
		for (Object principal : allPrincipals) {
			if (principal instanceof DiscodeitUserDetails discodeitUserDetails && discodeitUserDetails.getUserDto()
				.id()
				.equals(userId)) {
				List<SessionInformation> userSessions = sessionRegistry.getAllSessions(discodeitUserDetails, false);
				sessions.addAll(userSessions);
			}
		}
		return sessions;
	}

	public void invalidateSessionByUserId(UUID userId) {
		List<SessionInformation> sessionsByUserId = getSessionsByUserId(userId);

		if (!sessionsByUserId.isEmpty()) {
			sessionsByUserId.forEach(SessionInformation::expireNow);
		}
	}

	public boolean hasActiveSessions(UUID userId) {
		return !getSessionsByUserId(userId).isEmpty();
	}
}
