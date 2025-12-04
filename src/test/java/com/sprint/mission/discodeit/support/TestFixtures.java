package com.sprint.mission.discodeit.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.common.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.domain.dto.auth.data.UserDetailsDto;
import com.sprint.mission.discodeit.domain.dto.user.data.UserDto;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.ChannelType;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.domain.entity.Notification;
import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.domain.entity.Role;
import com.sprint.mission.discodeit.domain.entity.User;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.UUID;

/**
 * 통합 테스트 및 컨트롤러 테스트에서 사용하는 공통 테스트 픽스처.
 */
public final class TestFixtures {

    // ========== Common Test Constants ==========
    public static final String TEST_USERNAME = "testuser";
    public static final String TEST_EMAIL = "test@example.com";
    public static final String TEST_PASSWORD = "password123";
    public static final String ENCODED_PASSWORD = "encodedPassword123456";

    public static final String TEST_CHANNEL_NAME = "general";
    public static final String TEST_CHANNEL_DESCRIPTION = "General discussion channel";

    public static final long TEST_FILE_SIZE_SMALL = 512L;
    public static final long TEST_FILE_SIZE_MEDIUM = 1024L;
    public static final long TEST_FILE_SIZE_LARGE = 2048L;

    public static final String TEST_FILE_NAME = "test.txt";
    public static final String TEST_IMAGE_NAME = "test.png";
    public static final String TEST_CONTENT_TYPE_TEXT = "text/plain";
    public static final String TEST_CONTENT_TYPE_IMAGE = "image/png";
    public static final String TEST_CONTENT_TYPE_PDF = "application/pdf";

    // ========== SQL and ID Constants ==========
    public static final UUID MOCK_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final String MOCK_USER_INSERT_SQL =
        "INSERT INTO users (id, username, email, password, role, created_at, updated_at) "
            + "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";

    private TestFixtures() {
        throw new AssertionError("Utility class");
    }

    // ========== MockMultipartFile Helpers ==========
    public static MockMultipartFile createJsonRequestPart(
        String partName, Object request, ObjectMapper objectMapper) throws JsonProcessingException {
        return new MockMultipartFile(
            partName,
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );
    }

    public static MockMultipartFile createFilePart(
        String partName, String fileName, String contentType, byte[] content) {
        return new MockMultipartFile(partName, fileName, contentType, content);
    }

    public static void setSecurityContextForUser(User user) {
        UserDetailsDto userDetailsDto = new UserDetailsDto(
            user.getId(),
            user.getUsername(),
            user.getRole()
        );
        DiscodeitUserDetails principal = new DiscodeitUserDetails(userDetailsDto, "password");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            principal, principal.getPassword(), principal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public static User createUser(String username) {
        return new User(username, username + "@example.com", "encoded", null);
    }

    public static User createUser(String username, String email) {
        return new User(username, email, "encoded", null);
    }

    public static User createUserWithId(UUID id, String username) {
        User user = new User(username, username + "@example.com", "encodedPassword123456", null);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    public static User createUserWithId(UUID id, String username, String email, String password) {
        User user = new User(username, email, password, null);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    public static User createUserWithId(
        UUID id, String username, String email, String password, BinaryContent profile) {
        User user = new User(username, email, password, profile);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    public static Channel createPublicChannel(String name) {
        return new Channel(ChannelType.PUBLIC, name, null);
    }

    public static Channel createPublicChannel(String name, String description) {
        return new Channel(ChannelType.PUBLIC, name, description);
    }

    public static Channel createPrivateChannel() {
        return new Channel(ChannelType.PRIVATE, null, null);
    }

    public static Channel createChannelWithId(UUID id, ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        ReflectionTestUtils.setField(channel, "id", id);
        return channel;
    }

    public static BinaryContent createBinaryContentWithId(
        UUID id, String fileName, long size, String contentType) {
        BinaryContent content = new BinaryContent(fileName, size, contentType);
        ReflectionTestUtils.setField(content, "id", id);
        return content;
    }

    public static Message createMessageWithId(UUID id, String content, Channel channel, User author) {
        Message message = new Message(content, channel, author);
        ReflectionTestUtils.setField(message, "id", id);
        ReflectionTestUtils.setField(message, "createdAt", Instant.now());
        ReflectionTestUtils.setField(message, "updatedAt", Instant.now());
        return message;
    }

    public static ReadStatus createReadStatus(User user, Channel channel) {
        return new ReadStatus(user, channel, Instant.now(), false);
    }

    public static ReadStatus createReadStatus(User user, Channel channel, Instant lastReadAt) {
        return new ReadStatus(user, channel, lastReadAt, false);
    }

    public static ReadStatus createReadStatus(User user, Channel channel, boolean notificationEnabled) {
        return new ReadStatus(user, channel, Instant.now(), notificationEnabled);
    }

    public static ReadStatus createReadStatusWithId(UUID id, User user, Channel channel, Instant lastReadAt) {
        boolean notificationEnabled = channel.getType() == ChannelType.PRIVATE;
        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt, notificationEnabled);
        ReflectionTestUtils.setField(readStatus, "id", id);
        return readStatus;
    }

    public static Notification createNotificationWithId(UUID id, User receiver, String title, String content) {
        Notification notification = new Notification(receiver, title, content);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", Instant.now());
        return notification;
    }

    public static BinaryContent createBinaryContent(String fileName, long size, String contentType) {
        return new BinaryContent(fileName, size, contentType);
    }

    public static BinaryContent createBinaryContentWithId(UUID id) {
        return createBinaryContentWithId(id, "test.txt", 1024L, "text/plain");
    }

    public static Message createMessage(String content, Channel channel, User author) {
        return new Message(content, channel, author);
    }

    public static UserDto createUserDto(UUID id, String username, String email) {
        return createUserDto(id, username, email, Role.USER);
    }

    public static UserDto createUserDto(UUID id, String username, String email, Role role) {
        return new UserDto(id, username, email, null, true, role);
    }

    public static DiscodeitUserDetails createDiscodeitUserDetails(UserDetailsDto userDetailsDto) {
        return new DiscodeitUserDetails(userDetailsDto, "password");
    }

    public static DiscodeitUserDetails createDiscodeitUserDetails(UserDetailsDto userDetailsDto, String password) {
        return new DiscodeitUserDetails(userDetailsDto, password);
    }
}
