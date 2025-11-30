package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

public final class TestFixtures {

    private TestFixtures() {
        throw new AssertionError("Utility class");
    }

    public static User createUser(
        UUID userId,
        String username,
        String email,
        String password,
        BinaryContent profile
    ) {
        User user = new User(username, email, password, profile);
        ReflectionTestUtils.setField(user, "id", userId);
        return user;
    }

    public static User createUser(
        UUID userId,
        String username,
        String email,
        String password
    ) {
        return createUser(
            userId,
            username,
            email,
            password,
            null
        );
    }

    public static BinaryContent createBinaryContent(
        UUID binaryContentId,
        String fileName,
        long size,
        String contentType
    ) {
        BinaryContent content = new BinaryContent(fileName, size, contentType);
        ReflectionTestUtils.setField(content, "id", binaryContentId);
        return content;
    }
}
