package com.sprint.mission.discodeit.security.handler;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("failureHandler")
public class LoginFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {

		String errorMessage = "Invalid Username or Password";

		if (exception instanceof BadCredentialsException) {
			errorMessage = "Invalid Username or Password";
		} else if (exception instanceof UsernameNotFoundException) {
			errorMessage = "User not exists";
		} else if (exception instanceof CredentialsExpiredException) {
			errorMessage = "Expired password";
		}

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().write(errorMessage);
	}
}
