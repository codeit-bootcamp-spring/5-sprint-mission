package com.sprint.mission.discodeit.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

public class CustomRememberMeAuthenticationFilter extends OncePerRequestFilter {

  private final RememberMeServices rememberMeServices;

  public CustomRememberMeAuthenticationFilter(RememberMeServices rememberMeServices) {
    this.rememberMeServices = rememberMeServices;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    if (SecurityContextHolder.getContext()
                             .getAuthentication() == null) {
      Authentication auth = rememberMeServices.autoLogin(request, response);
      if (auth != null) {
        SecurityContextHolder.getContext()
                             .setAuthentication(auth);

        request.getSession(true)
               .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                   SecurityContextHolder.getContext());
      }
    }

    filterChain.doFilter(request, response);
  }
}