package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final SessionRegistry sessionRegistry;

    /**
     * 특정 사용자의 모든 세션을 만료시키는 메서드
     */
    public void expireUserSessions(String username) {
        sessionRegistry.getAllPrincipals().forEach(principal -> {
            if (principal instanceof UserDetails userDetails && userDetails.getUsername().equals(username)) {
                sessionRegistry.getAllSessions(principal, false)
                        .forEach(SessionInformation::expireNow);
            }
        });
    }
}
