package com.sprint.mission.discodeit.security.userdetails;

import com.sprint.mission.discodeit.dto.user.data.UserDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.UUID;

public class WithMockDiscodeitUserSecurityContextFactory
    implements WithSecurityContextFactory<WithMockDiscodeitUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockDiscodeitUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserDto userDto = new UserDto(
            UUID.fromString(annotation.userId()),
            annotation.username(),
            annotation.email(),
            null,
            true,
            annotation.role()
        );

        DiscodeitUserDetails principal = new DiscodeitUserDetails(userDto, "password");

        Authentication auth = new UsernamePasswordAuthenticationToken(
            principal,
            principal.getPassword(),
            principal.getAuthorities()
        );

        context.setAuthentication(auth);
        return context;
    }
}
