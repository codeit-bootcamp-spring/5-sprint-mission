package com.sprint.mission.discodeit.security.jwt;

import static com.sprint.mission.discodeit.exception.ErrorCode.*;
import static jakarta.servlet.http.HttpServletResponse.*;
import static org.springframework.http.MediaType.*;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.domain.dto.jwt.JwtDto;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {
	private final ObjectMapper objectMapper;
	private final JwtTokenProvider jwtTokenProvider;
	private final JwtRegistry jwtRegistry;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	  Authentication authentication) throws IOException, ServletException {

		DiscodeitUserDetails userDetails = (DiscodeitUserDetails)authentication.getPrincipal();
		try {
			UserDto userDto = userDetails.getUserDto();
			String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
			String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
			Cookie refreshCookie = jwtTokenProvider.genereateRefreshTokenCookie(refreshToken);

			JwtDto jwtDto = new JwtDto(userDto, accessToken);

			JwtInformation jwtInformation = new JwtInformation(userDto, accessToken, refreshToken);
			jwtRegistry.registerJwtInformation(jwtInformation);

			response.addCookie(refreshCookie);
			response.setContentType(APPLICATION_JSON_VALUE);
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

		} catch (JOSEException e) {
			log.error("Failed to generate access token In login success", e);
			int status = SC_INTERNAL_SERVER_ERROR;

			response.setStatus(status);
			ErrorResponse errorResponse = ErrorResponse.of(ACCESS_TOKEN_CREATE_FAIL, status, e);
			response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
		}

		response.getWriter()
		  .write(objectMapper.writeValueAsString(userDetails.getUserDto()));

	}

}
