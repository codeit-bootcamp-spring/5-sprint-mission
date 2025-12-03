package com.sprint.mission.discodeit.security.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class SecurityMatchers {

	public static final RequestMatcher NON_API =
		new NegatedRequestMatcher(
			PathPatternRequestMatcher.withDefaults().matcher("/api/**")
		);

	public static final RequestMatcher GET_CSRF_TOKEN =
		PathPatternRequestMatcher.withDefaults()
			.matcher(HttpMethod.GET, "/api/auth/csrf-token");

	public static final RequestMatcher SIGN_UP =
		PathPatternRequestMatcher.withDefaults()
			.matcher(HttpMethod.POST, "/api/users");

	public static final RequestMatcher LOGIN =
		PathPatternRequestMatcher.withDefaults()
			.matcher(HttpMethod.POST, "/api/auth/login");

	public static final String LOGIN_URL = "/api/auth/login";

	public static final RequestMatcher LOGOUT =
		PathPatternRequestMatcher.withDefaults()
			.matcher(HttpMethod.POST, "/api/auth/logout");

	public static final RequestMatcher ME =
		PathPatternRequestMatcher.withDefaults()
			.matcher(HttpMethod.GET, "/api/auth/me");

	public static final RequestMatcher REFRESH =
		PathPatternRequestMatcher.withDefaults()
			.matcher(HttpMethod.POST, "/api/auth/refresh");

	public static final RequestMatcher[] PUBLIC_MATCHERS = new RequestMatcher[] {
		NON_API, GET_CSRF_TOKEN, SIGN_UP, LOGIN, LOGOUT, ME, REFRESH
	};
}
