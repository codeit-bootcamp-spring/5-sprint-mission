package com.sprint.mission.discodeit.security;

import java.util.ArrayList;
import java.util.List;

public class SecurityWhitelist {
	// 공통 리스트
	private static final List<String> COMMON_LIST = List.of(
	  "/api/auth/login",
	  "/api/auth/refresh",
	  "/api/auth/csrf-token",
	  "/error",
	  "/",
	  "/index.html",
	  "/swagger-ui/",
	  "/favicon.ico",
	  "/css/**",
	  "/js/**",
	  "/images/**",
	  "/webjars/**",
	  "/favicon.ico",
	  "/swagger-ui/**",
	  "/assets/**",
	  "/actuator/**"
	);

	// WHITE_LIST = 공통 리스트 그대로
	public static final List<String> WHITE_LIST = COMMON_LIST;

	// JWT_SKIP_LIST = 공통 + 추가 스킵 리스트
	public static final List<String> JWT_SKIP_LIST = new ArrayList<>() {{
		addAll(COMMON_LIST);
		add("/api/users");
	}};

	public static final List<String> JWT_SKIP_LIST_START_WITH = List.of(
	  "/css/",
	  "/js/",
	  "/images/",
	  "/webjars/",
	  "/swagger-ui/",
	  "/assets/",
	  "/actuator/"
	);

	public static boolean matchesWhiteList(String path) {
		return WHITE_LIST.stream().anyMatch(white -> white.equals(path));
	}

	public static boolean matchesJwtWhiteList(String path) {
		return JWT_SKIP_LIST.stream().anyMatch(white -> white.equals(path)) ||
		  JWT_SKIP_LIST_START_WITH.stream().anyMatch(path::startsWith);
	}
}
