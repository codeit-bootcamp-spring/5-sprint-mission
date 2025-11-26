package com.sprint.mission.discodeit.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionManager {

    private final SessionRegistry sessionRegistry;

    public List<SessionInformation> getActiveSessionsByUserId(UUID userId) {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(principal -> principal instanceof DiscodeitUserDetails)
                .map(DiscodeitUserDetails.class::cast)
                .filter(details ->
                        details.getUserResponse() != null
                                && userId.equals(details.getUserResponse().getId())
                )
                .flatMap(details -> sessionRegistry.getAllSessions(details, false).stream())
                .toList();
    }

    public void invalidateSessionsByUserId(UUID userId) {
        List<SessionInformation> activeSessions = getActiveSessionsByUserId(userId);
        if (!activeSessions.isEmpty()) {
            activeSessions.forEach(SessionInformation::expireNow);
            log.info("[AuthService] 세션 무효화 개수: {}", activeSessions.size());
        } else {
            log.info("[AuthService] 무효화할 세션 없음: userId={}", userId);
        }
    }

}
