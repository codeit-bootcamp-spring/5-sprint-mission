package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.data.MessageDto;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.pagination.request.Pageable;
import com.sprint.mission.discodeit.dto.pagination.response.PageResponse;
import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageAttachment;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.binarycontent.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.message.MessageDeletedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentUploadException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageAttachmentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.support.TestFixtures.TEST_CHANNEL_NAME;
import static com.sprint.mission.discodeit.support.TestFixtures.TEST_EMAIL;
import static com.sprint.mission.discodeit.support.TestFixtures.TEST_USERNAME;
import static com.sprint.mission.discodeit.support.TestFixtures.createPublicChannel;
import static com.sprint.mission.discodeit.support.TestFixtures.createUser;
import static com.sprint.mission.discodeit.support.TestFixtures.createUserDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private MessageAttachmentRepository messageAttachmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private MultipartFile attachmentFile;

    @InjectMocks
    private MessageService messageService;

    @Test
    @DisplayName("create - 첨부파일 없이 메시지 생성 성공")
    void create_WithoutAttachments_Success() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest(
            "  Hello World  ",
            channelId,
            authorId
        );

        Channel channel = createPublicChannel(TEST_CHANNEL_NAME);
        User author = createUser(TEST_USERNAME);
        Message savedMessage = new Message("Hello World", channel, author);

        MessageDto expectedDto = new MessageDto(
            UUID.randomUUID(),
            Instant.now(),
            null,
            "Hello World",
            channelId,
            null,
            List.of()
        );

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(messageRepository.save(any(Message.class))).willReturn(savedMessage);
        given(messageMapper.toDto(any(Message.class), anyList())).willReturn(expectedDto);

        // when
        MessageDto result = messageService.create(request, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).isEqualTo("Hello World");

        then(channelRepository).should().findById(channelId);
        then(userRepository).should().findById(authorId);
        then(messageRepository).should().save(any(Message.class));
        then(binaryContentRepository).should(never()).save(any(BinaryContent.class));
        then(messageMapper).should().toDto(any(Message.class), anyList());
    }

    @Test
    @DisplayName("create - 첨부파일과 함께 메시지 생성 성공")
    void create_WithAttachments_Success() throws IOException {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest(
            "Check this out",
            channelId,
            authorId
        );

        given(attachmentFile.isEmpty()).willReturn(false);
        given(attachmentFile.getOriginalFilename()).willReturn("image.png");
        given(attachmentFile.getSize()).willReturn(1024L);
        given(attachmentFile.getContentType()).willReturn("image/png");
        given(attachmentFile.getBytes()).willReturn(new byte[]{1, 2, 3});

        Channel channel = createPublicChannel(TEST_CHANNEL_NAME);
        User author = createUser(TEST_USERNAME);
        Message savedMessage = new Message("Check this out", channel, author);
        BinaryContent savedAttachment = new BinaryContent("image.png", 1024L, "image/png");

        MessageDto expectedDto = new MessageDto(
            UUID.randomUUID(),
            Instant.now(),
            null,
            "Check this out",
            channelId,
            null,
            List.of()
        );

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(messageRepository.save(any(Message.class))).willReturn(savedMessage);
        given(messageMapper.toDto(any(Message.class), anyList())).willReturn(expectedDto);

        // when
        MessageDto result = messageService.create(request, List.of(attachmentFile));

        // then
        assertThat(result).isNotNull();

        then(channelRepository).should().findById(channelId);
        then(userRepository).should().findById(authorId);
        then(messageRepository).should().save(any(Message.class));
        then(binaryContentRepository).should().saveAll(anyList());
        then(eventPublisher).should().publishEvent(any(BinaryContentCreatedEvent.class));
        then(messageAttachmentRepository).should().saveAll(anyList());
    }

    @Test
    @DisplayName("create - 존재하지 않는 채널에 메시지 생성 시 ChannelNotFoundException 발생")
    void create_ChannelNotFound_ThrowsChannelNotFoundException() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest(
            "Hello",
            channelId,
            authorId
        );

        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> messageService.create(request, null))
            .isInstanceOf(ChannelNotFoundException.class);

        then(channelRepository).should().findById(channelId);
        then(messageRepository).should(never()).save(any(Message.class));
    }

    @Test
    @DisplayName("create - 존재하지 않는 작성자로 메시지 생성 시 UserNotFoundException 발생")
    void create_UserNotFound_ThrowsUserNotFoundException() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest(
            "Hello",
            channelId,
            authorId
        );

        Channel channel = createPublicChannel(TEST_CHANNEL_NAME);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> messageService.create(request, null))
            .isInstanceOf(UserNotFoundException.class);

        then(channelRepository).should().findById(channelId);
        then(userRepository).should().findById(authorId);
        then(messageRepository).should(never()).save(any(Message.class));
    }

    @Test
    @DisplayName("create - 첨부파일 업로드 실패 시 BinaryContentUploadException 발생")
    void create_AttachmentUploadFails_ThrowsBinaryContentUploadException() throws IOException {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest(
            "Test",
            channelId,
            authorId
        );

        // Fail-Fast: getBytes()가 먼저 호출되므로 다른 메타데이터 stubbing 불필요
        given(attachmentFile.isEmpty()).willReturn(false);
        given(attachmentFile.getBytes()).willThrow(new IOException("Read error"));

        Channel channel = createPublicChannel(TEST_CHANNEL_NAME);
        User author = createUser(TEST_USERNAME);
        Message savedMessage = new Message("Test", channel, author);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(messageRepository.save(any(Message.class))).willReturn(savedMessage);

        // when & then
        assertThatThrownBy(() -> messageService.create(request, List.of(attachmentFile)))
            .isInstanceOf(BinaryContentUploadException.class)
            .hasCauseInstanceOf(IOException.class);

        // Fail-Fast: getBytes() 실패 시 DB 저장 전에 예외 발생
        then(messageRepository).should().save(any(Message.class));
        then(binaryContentRepository).should(never()).saveAll(anyList());
    }

    @Test
    @DisplayName("update - 메시지 수정 성공")
    void update_Success() {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("  Updated content  ");

        Channel channel = createPublicChannel(TEST_CHANNEL_NAME);
        User author = createUser(TEST_USERNAME);
        Message message = new Message("Old content", channel, author);

        MessageDto expectedDto = new MessageDto(
            messageId,
            Instant.now(),
            Instant.now(),
            "Updated content",
            UUID.randomUUID(),
            null,
            List.of()
        );

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(messageAttachmentRepository.findByMessageIdOrderByOrderIndexAsc(messageId)).willReturn(List.of());
        given(messageMapper.toDto(any(Message.class), anyList())).willReturn(expectedDto);

        // when
        MessageDto result = messageService.update(messageId, request);

        // then
        assertThat(result).isNotNull();

        then(messageRepository).should().findById(messageId);
        then(messageAttachmentRepository).should().findByMessageIdOrderByOrderIndexAsc(messageId);
        then(messageMapper).should().toDto(any(Message.class), anyList());
    }

    @Test
    @DisplayName("update - 존재하지 않는 메시지 수정 시 MessageNotFoundException 발생")
    void update_MessageNotFound_ThrowsMessageNotFoundException() {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("Updated");

        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> messageService.update(messageId, request))
            .isInstanceOf(MessageNotFoundException.class);

        then(messageRepository).should().findById(messageId);
    }

    @Test
    @DisplayName("delete - 메시지 삭제 성공")
    void delete_Success() {
        // given
        UUID messageId = UUID.randomUUID();

        Channel channel = createPublicChannel(TEST_CHANNEL_NAME);
        User author = createUser(TEST_USERNAME);
        Message message = new Message("content", channel, author);

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        // when
        messageService.delete(messageId);

        // then
        then(messageRepository).should().findById(messageId);
        then(messageRepository).should().deleteById(messageId);
        then(eventPublisher).should().publishEvent(any(MessageDeletedEvent.class));
    }

    @Test
    @DisplayName("delete - 존재하지 않는 메시지 삭제 시 MessageNotFoundException 발생")
    void delete_MessageNotFound_ThrowsMessageNotFoundException() {
        // given
        UUID messageId = UUID.randomUUID();

        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> messageService.delete(messageId))
            .isInstanceOf(MessageNotFoundException.class);

        then(messageRepository).should().findById(messageId);
        then(messageRepository).should(never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("create - content가 null인 경우 null로 저장")
    void create_WithNullContent_Success() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest(
            null,
            channelId,
            authorId
        );

        Channel channel = createPublicChannel(TEST_CHANNEL_NAME);
        User author = createUser(TEST_USERNAME);
        Message savedMessage = new Message(null, channel, author);

        MessageDto expectedDto = new MessageDto(
            UUID.randomUUID(),
            Instant.now(),
            null,
            null,
            channelId,
            null,
            List.of()
        );

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(messageRepository.save(any(Message.class))).willReturn(savedMessage);
        given(messageMapper.toDto(any(Message.class), anyList())).willReturn(expectedDto);

        // when
        MessageDto result = messageService.create(request, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).isNull();

        then(messageRepository).should().save(any(Message.class));
    }

    @Test
    @DisplayName("create - 빈 첨부파일 목록으로 생성 시 첨부파일 저장 안함")
    void create_WithEmptyAttachmentsList_Success() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest(
            "Hello",
            channelId,
            authorId
        );

        Channel channel = createPublicChannel(TEST_CHANNEL_NAME);
        User author = createUser(TEST_USERNAME);
        Message savedMessage = new Message("Hello", channel, author);

        MessageDto expectedDto = new MessageDto(
            UUID.randomUUID(),
            Instant.now(),
            null,
            "Hello",
            channelId,
            null,
            List.of()
        );

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(messageRepository.save(any(Message.class))).willReturn(savedMessage);
        given(messageMapper.toDto(any(Message.class), anyList())).willReturn(expectedDto);

        // when
        MessageDto result = messageService.create(request, List.of());

        // then
        assertThat(result).isNotNull();

        then(binaryContentRepository).should(never()).save(any(BinaryContent.class));
        then(messageAttachmentRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("create - 첨부파일 목록에 null/빈 파일이 포함된 경우 스킵")
    void create_WithNullOrEmptyAttachments_SkipsNullAndEmpty() throws IOException {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest(
            "Test",
            channelId,
            authorId
        );

        MultipartFile emptyFile = mock(MultipartFile.class);
        given(emptyFile.isEmpty()).willReturn(true);

        given(attachmentFile.isEmpty()).willReturn(false);
        given(attachmentFile.getOriginalFilename()).willReturn("valid.png");
        given(attachmentFile.getSize()).willReturn(1024L);
        given(attachmentFile.getContentType()).willReturn("image/png");
        given(attachmentFile.getBytes()).willReturn(new byte[]{1, 2, 3});

        Channel channel = createPublicChannel(TEST_CHANNEL_NAME);
        User author = createUser(TEST_USERNAME);
        Message savedMessage = new Message("Test", channel, author);
        BinaryContent savedAttachment = new BinaryContent("valid.png", 1024L, "image/png");

        MessageDto expectedDto = new MessageDto(
            UUID.randomUUID(),
            Instant.now(),
            null,
            "Test",
            channelId,
            null,
            List.of()
        );

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(messageRepository.save(any(Message.class))).willReturn(savedMessage);
        given(messageMapper.toDto(any(Message.class), anyList())).willReturn(expectedDto);

        // when - 빈 파일을 포함한 목록 전달
        MessageDto result = messageService.create(request, List.of(emptyFile, attachmentFile));

        // then - 유효한 파일만 저장됨 (1번만 호출)
        assertThat(result).isNotNull();

        then(binaryContentRepository).should().saveAll(anyList());
        then(messageAttachmentRepository).should().saveAll(anyList());
    }

    @Test
    @DisplayName("create - 첨부파일 목록에 null이 포함된 경우 스킵")
    void create_WithNullAttachment_SkipsNull() throws IOException {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest(
            "Test",
            channelId,
            authorId
        );

        given(attachmentFile.isEmpty()).willReturn(false);
        given(attachmentFile.getOriginalFilename()).willReturn("valid.png");
        given(attachmentFile.getSize()).willReturn(1024L);
        given(attachmentFile.getContentType()).willReturn("image/png");
        given(attachmentFile.getBytes()).willReturn(new byte[]{1, 2, 3});

        Channel channel = createPublicChannel(TEST_CHANNEL_NAME);
        User author = createUser(TEST_USERNAME);
        Message savedMessage = new Message("Test", channel, author);
        BinaryContent savedAttachment = new BinaryContent("valid.png", 1024L, "image/png");

        MessageDto expectedDto = new MessageDto(
            UUID.randomUUID(),
            Instant.now(),
            null,
            "Test",
            channelId,
            null,
            List.of()
        );

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(messageRepository.save(any(Message.class))).willReturn(savedMessage);
        given(messageMapper.toDto(any(Message.class), anyList())).willReturn(expectedDto);

        // when - null을 포함한 목록 전달 (Arrays.asList는 null 허용)
        MessageDto result = messageService.create(request, Arrays.asList(null, attachmentFile));

        // then - null은 스킵되고 유효한 파일만 저장됨
        assertThat(result).isNotNull();

        then(binaryContentRepository).should().saveAll(anyList());
        then(messageAttachmentRepository).should().saveAll(anyList());
    }

    @Test
    @DisplayName("findAllByChannelId - 첨부파일이 있는 메시지 조회")
    void findAllByChannelId_WithAttachments_MapsAttachmentsCorrectly() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID attachmentId = UUID.randomUUID();
        Pageable pageable = new Pageable(0, 10, null);
        Instant messageCreatedAt = Instant.now();

        Channel channel = createPublicChannel(TEST_CHANNEL_NAME);
        User author = createUser(TEST_USERNAME);
        setField(author, "id", authorId);

        Message message = new Message("Test with attachment", channel, author);
        setField(message, "id", messageId);
        setField(message, "createdAt", messageCreatedAt);

        BinaryContent attachment = new BinaryContent("file.png", 1024L, "image/png");
        setField(attachment, "id", attachmentId);

        MessageAttachment messageAttachment = new MessageAttachment(message, attachment, 0);

        Page<Message> page = new PageImpl<>(List.of(message), PageRequest.of(0, 10), 1);

        UserDto authorDto = createUserDto(authorId, TEST_USERNAME, TEST_EMAIL);
        MessageDto expectedDto = new MessageDto(
            messageId,
            messageCreatedAt,
            null,
            "Test with attachment",
            channelId,
            authorDto,
            List.of()
        );

        given(messageRepository.findByChannelId(eq(channelId), any(PageRequest.class)))
            .willReturn(page);
        given(messageAttachmentRepository.findByMessageInOrderByOrderIndexAsc(anyList()))
            .willReturn(List.of(messageAttachment));
        given(userMapper.toDto(author)).willReturn(authorDto);
        given(messageMapper.toDtoWithAuthorDto(eq(message), eq(authorDto), anyList())).willReturn(expectedDto);

        // when
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, null, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);

        then(messageAttachmentRepository).should().findByMessageInOrderByOrderIndexAsc(anyList());
        then(messageMapper).should().toDtoWithAuthorDto(eq(message), eq(authorDto), anyList());
    }

    @Test
    @DisplayName("findAllByChannelId - cursor가 있을 때 findAllByChannelId 호출")
    void findAllByChannelId_WithCursor_UsesPageByChannelId() {
        // given
        UUID channelId = UUID.randomUUID();
        Instant cursor = Instant.now();
        Pageable pageable = new Pageable(0, 10, null);

        Page<Message> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        given(messageRepository.findByChannelIdAndCreatedAtBefore(eq(channelId), eq(cursor), any(PageRequest.class)))
            .willReturn(emptyPage);

        // when
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, cursor, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).isEmpty();

        then(messageRepository).should().findByChannelIdAndCreatedAtBefore(
            eq(channelId), eq(cursor), any(PageRequest.class));
        then(messageRepository).should(never()).findByChannelId(any(), any());
    }

    @Test
    @DisplayName("findAllByChannelId - cursor가 null일 때 findPageWithoutCursorByChannelId 호출")
    void findAllByChannelId_WithoutCursor_UsesPageWithoutCursor() {
        // given
        UUID channelId = UUID.randomUUID();
        Pageable pageable = new Pageable(0, 10, null);

        Page<Message> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        given(messageRepository.findByChannelId(eq(channelId), any(PageRequest.class)))
            .willReturn(emptyPage);

        // when
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, null, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).isEmpty();

        then(messageRepository).should().findByChannelId(eq(channelId), any(PageRequest.class));
        then(messageRepository).should(never()).findByChannelIdAndCreatedAtBefore(any(), any(), any());
    }

    @Test
    @DisplayName("findAllByChannelId - author가 null인 메시지 처리")
    void findAllByChannelId_WithNullAuthor_ReturnsNullAuthorDto() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        Pageable pageable = new Pageable(0, 10, null);
        Instant messageCreatedAt = Instant.now();

        // Message 엔티티는 author null을 허용하지 않으므로 mock 사용
        Message message = mock(Message.class);
        given(message.getId()).willReturn(messageId);
        given(message.getAuthor()).willReturn(null);

        Page<Message> page = new PageImpl<>(List.of(message), PageRequest.of(0, 10), 1);

        MessageDto expectedDto = new MessageDto(
            messageId,
            messageCreatedAt,
            null,
            "Test",
            channelId,
            null,
            List.of()
        );

        given(messageRepository.findByChannelId(eq(channelId), any(PageRequest.class)))
            .willReturn(page);
        given(messageAttachmentRepository.findByMessageInOrderByOrderIndexAsc(anyList())).willReturn(List.of());
        given(messageMapper.toDtoWithAuthorDto(eq(message), eq(null), any())).willReturn(expectedDto);

        // when
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, null, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).author()).isNull();

        then(userMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("findAllByChannelId - hasNext가 true일 때 nextCursor 반환")
    void findAllByChannelId_HasNext_ReturnsNextCursor() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        Pageable pageable = new Pageable(0, 1, null);
        Instant messageCreatedAt = Instant.parse("2024-01-01T10:00:00Z");

        Channel channel = createPublicChannel(TEST_CHANNEL_NAME);
        User author = createUser(TEST_USERNAME);
        setField(author, "id", authorId);

        Message message = new Message("Test", channel, author);
        setField(message, "id", messageId);
        setField(message, "createdAt", messageCreatedAt);

        // hasNext = true 인 페이지 생성
        Page<Message> page = new PageImpl<>(List.of(message), PageRequest.of(0, 1), 2);

        UserDto authorDto = createUserDto(authorId, TEST_USERNAME, TEST_EMAIL);
        MessageDto expectedDto = new MessageDto(
            messageId,
            messageCreatedAt,
            null,
            "Test",
            channelId,
            authorDto,
            List.of()
        );

        given(messageRepository.findByChannelId(eq(channelId), any(PageRequest.class)))
            .willReturn(page);
        given(messageAttachmentRepository.findByMessageInOrderByOrderIndexAsc(anyList())).willReturn(List.of());
        given(userMapper.toDto(author)).willReturn(authorDto);
        given(messageMapper.toDtoWithAuthorDto(eq(message), eq(authorDto), any())).willReturn(expectedDto);

        // when
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, null, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursor()).isEqualTo(messageCreatedAt);
    }

    @Test
    @DisplayName("findAllByChannelId - hasNext가 false일 때 nextCursor는 null")
    void findAllByChannelId_NoNext_ReturnsNullCursor() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        Pageable pageable = new Pageable(0, 10, null);
        Instant messageCreatedAt = Instant.now();

        Channel channel = createPublicChannel(TEST_CHANNEL_NAME);
        User author = createUser(TEST_USERNAME);
        setField(author, "id", authorId);

        Message message = new Message("Test", channel, author);
        setField(message, "id", messageId);
        setField(message, "createdAt", messageCreatedAt);

        // hasNext = false 인 페이지 생성
        Page<Message> page = new PageImpl<>(List.of(message), PageRequest.of(0, 10), 1);

        UserDto authorDto = createUserDto(authorId, TEST_USERNAME, TEST_EMAIL);
        MessageDto expectedDto = new MessageDto(
            messageId,
            messageCreatedAt,
            null,
            "Test",
            channelId,
            authorDto,
            List.of()
        );

        given(messageRepository.findByChannelId(eq(channelId), any(PageRequest.class)))
            .willReturn(page);
        given(messageAttachmentRepository.findByMessageInOrderByOrderIndexAsc(anyList())).willReturn(List.of());
        given(userMapper.toDto(author)).willReturn(authorDto);
        given(messageMapper.toDtoWithAuthorDto(eq(message), eq(authorDto), any())).willReturn(expectedDto);

        // when
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, null, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.hasNext()).isFalse();
        assertThat(result.nextCursor()).isNull();
    }

    @Test
    @DisplayName("update - newContent가 null일 때 content를 업데이트하지 않음")
    void update_WithNullContent_DoesNotUpdate() {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest(null);

        Channel channel = createPublicChannel(TEST_CHANNEL_NAME);
        User author = createUser(TEST_USERNAME);
        Message message = new Message("Original content", channel, author);

        MessageDto expectedDto = new MessageDto(
            messageId,
            Instant.now(),
            null,
            "Original content",
            UUID.randomUUID(),
            null,
            List.of()
        );

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(messageAttachmentRepository.findByMessageIdOrderByOrderIndexAsc(messageId)).willReturn(List.of());
        given(messageMapper.toDto(any(Message.class), anyList())).willReturn(expectedDto);

        // when
        MessageDto result = messageService.update(messageId, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).isEqualTo("Original content");

        then(messageRepository).should().findById(messageId);
        then(messageMapper).should().toDto(any(Message.class), anyList());
    }
}
