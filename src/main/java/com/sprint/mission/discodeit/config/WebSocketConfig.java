package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.jwt.JwtAuthenticationChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageAuthorizationContext;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Collection;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtAuthenticationChannelInterceptor jwtAuthenticationChannelInterceptor;
    private final RoleHierarchy roleHierarchy;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // server -> client
        config.enableSimpleBroker("/sub");
        // client -> server
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
                jwtAuthenticationChannelInterceptor,
                new SecurityContextChannelInterceptor(),
                authorizationChannelInterceptor(roleHierarchy)
        );
    }

    private AuthorizationChannelInterceptor authorizationChannelInterceptor(RoleHierarchy roleHierarchy) {
        AuthorizationManager<MessageAuthorizationContext<?>> requireUser =
                hasReachableRole(roleHierarchy);

        return new AuthorizationChannelInterceptor(
                MessageMatcherDelegatingAuthorizationManager.builder()
                        .nullDestMatcher().permitAll()
                        .simpDestMatchers("/pub/**").access(requireUser)
                        .simpSubscribeDestMatchers("/sub/**").access(requireUser)
                        .anyMessage().denyAll()
                        .build()
        );
    }

    private static AuthorizationManager<MessageAuthorizationContext<?>> hasReachableRole(
            RoleHierarchy roleHierarchy
    ) {
        final String requiredAuthority = "ROLE_" + Role.USER.name();

        return (authenticationSupplier, context) -> {
            Authentication auth = authenticationSupplier.get();
            if (auth == null || !auth.isAuthenticated()) {
                return new AuthorizationDecision(false);
            }

            Collection<? extends GrantedAuthority> reachable =
                    roleHierarchy.getReachableGrantedAuthorities(auth.getAuthorities());

            boolean allowed = reachable.stream()
                    .anyMatch(a -> requiredAuthority.equals(a.getAuthority()));

            return new AuthorizationDecision(allowed);
        };
    }
}
