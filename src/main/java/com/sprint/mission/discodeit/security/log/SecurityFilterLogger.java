package com.sprint.mission.discodeit.security.log;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Component
public class SecurityFilterLogger {

  private final Logger log = LoggerFactory.getLogger(SecurityFilterLogger.class);
  private final FilterChainProxy filterChainProxy;

  public SecurityFilterLogger(FilterChainProxy filterChainProxy) {
    this.filterChainProxy = filterChainProxy;
  }

  @PostConstruct
  public void logFilters() {
    log.info("=== Spring Security Filter Chains ===");

    for (SecurityFilterChain chain : filterChainProxy.getFilterChains()) {
      if (chain instanceof DefaultSecurityFilterChain defaultChain) {
        log.info("----- Filter chain for matcher: {}", defaultChain.getRequestMatcher());
      } else {
        log.info("----- Filter chain: {}", chain.getClass()
                                                .getName());
      }

      try {
        chain.getFilters()
             .forEach(filter -> log.info("  - {}", filter.getClass()
                                                         .getName()));
      } catch (Exception e) {
        log.warn("  - cannot enumerate filters for chain: {}", chain, e);
      }
    }

    log.info("=== End of Filter Chains ===");
  }
}