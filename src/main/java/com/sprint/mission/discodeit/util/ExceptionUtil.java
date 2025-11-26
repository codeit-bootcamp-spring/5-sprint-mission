package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Locale;

public final class ExceptionUtil {

    private ExceptionUtil() {
        throw new AssertionError("Utility class");
    }

    public static void handleDuplicateUserConstraint(
        DataIntegrityViolationException e,
        String username,
        String email
    ) {
        String message = e.getMessage();
        if (message == null) {
            throw e;
        }

        String lowerMessage = message.toLowerCase(Locale.ROOT);

        if (lowerMessage.contains("users_email_key")) {
            throw new DuplicateEmailException(email);
        }

        if (lowerMessage.contains("users_username_key")) {
            throw new DuplicateUsernameException(username);
        }

        if (lowerMessage.contains("users(email")) {
            throw new DuplicateEmailException(email);
        }

        if (lowerMessage.contains("users(username")) {
            throw new DuplicateUsernameException(username);
        }

        throw e;
    }
}
