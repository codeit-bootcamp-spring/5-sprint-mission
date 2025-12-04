package com.sprint.mission.discodeit.global.security.userdetails;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockDiscodeitUserSecurityContextFactory
    implements WithSecurityContextFactory<WithMockDiscodeitUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockDiscodeitUser annotation) {
        return null;
    }
}
