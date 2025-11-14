package com.sprint.mission.discodeit.security;

import static com.sprint.mission.discodeit.exception.ErrorCode.*;
import static jakarta.servlet.http.HttpServletResponse.*;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	  AuthenticationException exception) throws IOException, ServletException {

		response.setStatus(SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
		ErrorResponse errorResponse = ErrorResponse
		  .of(LOGIN_FAIL, SC_UNAUTHORIZED, exception);

		response.getWriter()
		  .write(objectMapper.writeValueAsString(errorResponse));
	}
}
