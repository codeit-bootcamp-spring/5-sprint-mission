package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockDiscodeitUserSecurityContextFactory.class)
public @interface WithMockDiscodeitUser {

    String userId() default "00000000-0000-0000-0000-000000000001";

    String username() default "testuser";

    String email() default "test@example.com";

    Role role() default Role.USER;
}
