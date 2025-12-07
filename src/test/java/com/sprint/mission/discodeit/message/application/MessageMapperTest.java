package com.sprint.mission.discodeit.message.application;

import com.sprint.mission.discodeit.binarycontent.application.BinaryContentMapper;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStatus;
import com.sprint.mission.discodeit.binarycontent.presentation.dto.BinaryContentDto;
import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.message.domain.Message;
import com.sprint.mission.discodeit.message.presentation.dto.MessageDto;
import com.sprint.mission.discodeit.user.application.UserMapper;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageMapper 단위 테스트")
class MessageMapperTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private BinaryContentMapper binaryContentMapper;

    @InjectMocks
    private MessageMapper mapper;

    private static final UUID TEST_MESSAGE_ID = UUID.randomUUID();
    private static final UUID TEST_CHANNEL_ID = UUID.randomUUID();
    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final String TEST_CONTENT = "Hello, world!";
    private static final Instant TEST_CREATED_AT = Instant.now();
    private static final Instant TEST_UPDATED_AT = Instant.now();

    private User testUser;
    private Channel testChannel;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@example.com", "$2a$10$encrypted", null);
        ReflectionTestUtils.setField(testUser, "id", TEST_USER_ID);

        testChannel = new Channel(ChannelType.PUBLIC, "general", "General channel");
        ReflectionTestUtils.setField(testChannel, "id", TEST_CHANNEL_ID);

        testUserDto = new UserDto(TEST_USER_ID, "testuser", "test@example.com", null, true, Role.USER);
    }

    @Nested
    @DisplayName("toDto 메서드")
    class ToDto {

        @Test
        @DisplayName("첨부파일 없는 메시지를 MessageDto로 변환 성공")
        void toDto_withoutAttachments_returnsDto() {
            // given
            Message message = new Message(TEST_CONTENT, testChannel, testUser);
            ReflectionTestUtils.setField(message, "id", TEST_MESSAGE_ID);
            ReflectionTestUtils.setField(message, "createdAt", TEST_CREATED_AT);
            ReflectionTestUtils.setField(message, "updatedAt", TEST_UPDATED_AT);

            given(userMapper.toDto(testUser)).willReturn(testUserDto);

            // when
            MessageDto result = mapper.toDto(message, Collections.emptyList());

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(TEST_MESSAGE_ID);
            assertThat(result.createdAt()).isEqualTo(TEST_CREATED_AT);
            assertThat(result.updatedAt()).isEqualTo(TEST_UPDATED_AT);
            assertThat(result.content()).isEqualTo(TEST_CONTENT);
            assertThat(result.channelId()).isEqualTo(TEST_CHANNEL_ID);
            assertThat(result.author()).isEqualTo(testUserDto);
            assertThat(result.attachments()).isEmpty();
        }

        @Test
        @DisplayName("첨부파일 있는 메시지를 MessageDto로 변환 성공")
        void toDto_withAttachments_returnsDto() {
            // given
            Message message = new Message(TEST_CONTENT, testChannel, testUser);
            ReflectionTestUtils.setField(message, "id", TEST_MESSAGE_ID);
            ReflectionTestUtils.setField(message, "createdAt", TEST_CREATED_AT);
            ReflectionTestUtils.setField(message, "updatedAt", TEST_UPDATED_AT);

            UUID attachmentId = UUID.randomUUID();
            BinaryContent attachment = new BinaryContent("file.png", 2048L, "image/png");
            ReflectionTestUtils.setField(attachment, "id", attachmentId);

            BinaryContentDto attachmentDto = new BinaryContentDto(
                attachmentId, "file.png", 2048L, "image/png", BinaryContentStatus.SUCCESS
            );

            given(userMapper.toDto(testUser)).willReturn(testUserDto);
            given(binaryContentMapper.toDto(attachment)).willReturn(attachmentDto);

            // when
            MessageDto result = mapper.toDto(message, List.of(attachment));

            // then
            assertThat(result).isNotNull();
            assertThat(result.attachments()).hasSize(1);
            assertThat(result.attachments().get(0)).isEqualTo(attachmentDto);
        }

        @Test
        @DisplayName("null 메시지 입력 시 null 반환")
        void toDto_withNullMessage_returnsNull() {
            // when
            MessageDto result = mapper.toDto(null, Collections.emptyList());

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("여러 첨부파일이 있는 메시지 변환 성공")
        void toDto_withMultipleAttachments_returnsDto() {
            // given
            Message message = new Message(TEST_CONTENT, testChannel, testUser);
            ReflectionTestUtils.setField(message, "id", TEST_MESSAGE_ID);
            ReflectionTestUtils.setField(message, "createdAt", TEST_CREATED_AT);
            ReflectionTestUtils.setField(message, "updatedAt", TEST_UPDATED_AT);

            UUID attachmentId1 = UUID.randomUUID();
            UUID attachmentId2 = UUID.randomUUID();
            BinaryContent attachment1 = new BinaryContent("file1.png", 1024L, "image/png");
            BinaryContent attachment2 = new BinaryContent("file2.pdf", 2048L, "application/pdf");
            ReflectionTestUtils.setField(attachment1, "id", attachmentId1);
            ReflectionTestUtils.setField(attachment2, "id", attachmentId2);

            BinaryContentDto attachmentDto1 = new BinaryContentDto(
                attachmentId1, "file1.png", 1024L, "image/png", BinaryContentStatus.SUCCESS
            );
            BinaryContentDto attachmentDto2 = new BinaryContentDto(
                attachmentId2, "file2.pdf", 2048L, "application/pdf", BinaryContentStatus.SUCCESS
            );

            given(userMapper.toDto(testUser)).willReturn(testUserDto);
            given(binaryContentMapper.toDto(attachment1)).willReturn(attachmentDto1);
            given(binaryContentMapper.toDto(attachment2)).willReturn(attachmentDto2);

            // when
            MessageDto result = mapper.toDto(message, List.of(attachment1, attachment2));

            // then
            assertThat(result).isNotNull();
            assertThat(result.attachments()).hasSize(2);
            assertThat(result.attachments()).containsExactly(attachmentDto1, attachmentDto2);
        }
    }

    @Nested
    @DisplayName("toDtoWithAuthorDto 메서드")
    class ToDtoWithAuthorDto {

        @Test
        @DisplayName("미리 변환된 UserDto로 MessageDto 변환 성공")
        void toDtoWithAuthorDto_withPreConvertedUserDto_returnsDto() {
            // given
            Message message = new Message(TEST_CONTENT, testChannel, testUser);
            ReflectionTestUtils.setField(message, "id", TEST_MESSAGE_ID);
            ReflectionTestUtils.setField(message, "createdAt", TEST_CREATED_AT);
            ReflectionTestUtils.setField(message, "updatedAt", TEST_UPDATED_AT);

            // when
            MessageDto result = mapper.toDtoWithAuthorDto(message, testUserDto, Collections.emptyList());

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(TEST_MESSAGE_ID);
            assertThat(result.author()).isEqualTo(testUserDto);
            assertThat(result.channelId()).isEqualTo(TEST_CHANNEL_ID);
        }

        @Test
        @DisplayName("null 메시지 입력 시 null 반환")
        void toDtoWithAuthorDto_withNullMessage_returnsNull() {
            // when
            MessageDto result = mapper.toDtoWithAuthorDto(null, testUserDto, Collections.emptyList());

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("첨부파일과 함께 변환 성공")
        void toDtoWithAuthorDto_withAttachments_returnsDto() {
            // given
            Message message = new Message(TEST_CONTENT, testChannel, testUser);
            ReflectionTestUtils.setField(message, "id", TEST_MESSAGE_ID);
            ReflectionTestUtils.setField(message, "createdAt", TEST_CREATED_AT);
            ReflectionTestUtils.setField(message, "updatedAt", TEST_UPDATED_AT);

            UUID attachmentId = UUID.randomUUID();
            BinaryContent attachment = new BinaryContent("doc.pdf", 4096L, "application/pdf");
            ReflectionTestUtils.setField(attachment, "id", attachmentId);

            BinaryContentDto attachmentDto = new BinaryContentDto(
                attachmentId, "doc.pdf", 4096L, "application/pdf", BinaryContentStatus.SUCCESS
            );

            given(binaryContentMapper.toDto(attachment)).willReturn(attachmentDto);

            // when
            MessageDto result = mapper.toDtoWithAuthorDto(message, testUserDto, List.of(attachment));

            // then
            assertThat(result).isNotNull();
            assertThat(result.attachments()).hasSize(1);
            assertThat(result.attachments().get(0)).isEqualTo(attachmentDto);
        }
    }
}
