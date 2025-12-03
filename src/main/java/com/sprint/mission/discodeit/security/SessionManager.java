package com.sprint.mission.discodeit.security;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionManager {

  private final SessionRegistry sessionRegistry;

  public void expireSessionsForUser(String username) {

    List<Object> principals = sessionRegistry.getAllPrincipals();
    for (Object principal : principals) {
      if (principal instanceof DiscodeitUserDetails userDetails && userDetails.getUsername()
                                                                              .equals(username)) {

        List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails, false);
        for (SessionInformation sessionInfo : sessions) {
          sessionInfo.expireNow();
        }
      }
    }
  }

  public boolean isUserOnline(String username) {
    List<Object> principals = sessionRegistry.getAllPrincipals();
    for (Object principal : principals) {
      if (principal instanceof DiscodeitUserDetails ud && ud.getUsername()
                                                            .equals(username)) {
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(ud, false);
        if (!sessions.isEmpty()) {
          return true;
        }
      }
    }
    return false;
  }
}
