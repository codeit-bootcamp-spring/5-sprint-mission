package com.sprint.mission.discodeit.domain.dto.command;

import jakarta.servlet.http.HttpServletResponse;

public record GetNewAccTokenCommand(String refreshToken, HttpServletResponse response) {
	public static GetNewAccTokenCommand from(
	  String refreshToken,
	  HttpServletResponse response) {

		return new GetNewAccTokenCommand(refreshToken, response);
	}
}
