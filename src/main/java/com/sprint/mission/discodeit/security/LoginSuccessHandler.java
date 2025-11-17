package com.sprint.mission.discodeit.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
	private final ObjectMapper objectMapper;
	private final SessionRegistry sessionRegistry;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	  Authentication authentication) throws IOException, ServletException {

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);

		DiscodeitUserDetails userDetails = (DiscodeitUserDetails)authentication.getPrincipal();
		HttpSession currentSession = request.getSession(false);
		
		expireBeforeSessions(userDetails, currentSession);

		response.getWriter()
		  .write(objectMapper.writeValueAsString(userDetails.getUserDto()));
	}

	private void expireBeforeSessions(DiscodeitUserDetails userDetails, HttpSession currentSession) {
		List<DiscodeitUserDetails> targetUserDetails = sessionRegistry.getAllPrincipals().stream()
		  .filter(principal -> principal instanceof DiscodeitUserDetails) // 타입 체크
		  .map(principal -> (DiscodeitUserDetails)principal)             // 캐스팅
		  .filter(details -> details.getUsername().equals(userDetails.getUsername()))     // username 필터
		  .toList();

		List<SessionInformation> sessions = targetUserDetails.stream()
		  .map(details -> sessionRegistry.getAllSessions(details, false)) // List<SessionInformation>
		  .flatMap(List::stream)
		  .filter(session -> !session.getSessionId().equals(currentSession.getId()))
		  .toList();

		sessions.forEach(SessionInformation::expireNow);

	}
}
