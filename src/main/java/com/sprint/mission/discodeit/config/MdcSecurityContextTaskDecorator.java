package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.interceptor.MDCLoggingInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class MdcSecurityContextTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {

        String requestId = String.valueOf(MDC.get(MDCLoggingInterceptor.REQUEST_ID));
        SecurityContext securityContext = SecurityContextHolder.getContext();

        log.info("[Decorator] Captured requestId={} at decorate() time", requestId);
        log.info("[Decorator] Captured SecurityContext={} at decorate() time", securityContext.getAuthentication());

        return () -> {
            log.info("[Decorator Runnable] Restoring context... requestId={}, SecurityContext={}",
                    requestId,
                    securityContext.getAuthentication()
            );

            if (requestId != null) {
                MDC.put(MDCLoggingInterceptor.REQUEST_ID, requestId);
            }
            SecurityContextHolder.setContext(securityContext);

            try {
                runnable.run();
            } finally {
                log.info("[Decorator Runnable] Clearing context...");
                if (requestId != null) {
                    MDC.remove(MDCLoggingInterceptor.REQUEST_ID);
                }
                SecurityContextHolder.clearContext();
            }
        };
    }
}
