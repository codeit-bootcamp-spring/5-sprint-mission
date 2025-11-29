package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.data.MessageDto;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
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
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

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
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private UserMapper userMapper;

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

        Channel channel = new Channel(ChannelType.PUBLIC, "general", null);
        User author = new User("testuser", "test@example.com", "encoded", null);
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

        MultipartFile file = mock(MultipartFile.class);
        given(file.isEmpty()).willReturn(false);
        given(file.getOriginalFilename()).willReturn("image.png");
        given(file.getSize()).willReturn(1024L);
        given(file.getContentType()).willReturn("image/png");
        given(file.getBytes()).willReturn(new byte[]{1, 2, 3});

        Channel channel = new Channel(ChannelType.PUBLIC, "general", null);
        User author = new User("testuser", "test@example.com", "encoded", null);
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
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(savedAttachment);
        given(messageMapper.toDto(any(Message.class), anyList())).willReturn(expectedDto);

        // when
        MessageDto result = messageService.create(request, List.of(file));

        // then
        assertThat(result).isNotNull();

        then(channelRepository).should().findById(channelId);
        then(userRepository).should().findById(authorId);
        then(messageRepository).should().save(any(Message.class));
        then(binaryContentRepository).should().save(any(BinaryContent.class));
        then(binaryContentStorage).should().put(any(), any(byte[].class));
        then(messageAttachmentRepository).should().save(any());
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

        Channel channel = new Channel(ChannelType.PUBLIC, "general", null);

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

        MultipartFile file = mock(MultipartFile.class);
        given(file.isEmpty()).willReturn(false);
        given(file.getOriginalFilename()).willReturn("file.txt");
        given(file.getSize()).willReturn(100L);
        given(file.getContentType()).willReturn("text/plain");
        given(file.getBytes()).willThrow(new IOException("Read error"));

        Channel channel = new Channel(ChannelType.PUBLIC, "general", null);
        User author = new User("testuser", "test@example.com", "encoded", null);
        Message savedMessage = new Message("Test", channel, author);
        BinaryContent savedAttachment = new BinaryContent("file.txt", 100L, "text/plain");

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(messageRepository.save(any(Message.class))).willReturn(savedMessage);
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(savedAttachment);

        // when & then
        assertThatThrownBy(() -> messageService.create(request, List.of(file)))
            .isInstanceOf(BinaryContentUploadException.class)
            .hasCauseInstanceOf(IOException.class);

        // 메시지와 BinaryContent 메타데이터는 저장 시도됨 (트랜잭션 롤백으로 실제로는 저장되지 않음)
        then(messageRepository).should().save(any(Message.class));
        then(binaryContentRepository).should().save(any(BinaryContent.class));
    }

    @Test
    @DisplayName("update - 메시지 수정 성공")
    void update_Success() {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("  Updated content  ");

        Channel channel = new Channel(ChannelType.PUBLIC, "general", null);
        User author = new User("testuser", "test@example.com", "encoded", null);
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
        given(messageAttachmentRepository.findAttachmentsByMessageId(messageId)).willReturn(List.of());
        given(messageMapper.toDto(any(Message.class), anyList())).willReturn(expectedDto);

        // when
        MessageDto result = messageService.update(messageId, request);

        // then
        assertThat(result).isNotNull();

        then(messageRepository).should().findById(messageId);
        then(messageAttachmentRepository).should().findAttachmentsByMessageId(messageId);
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

        Channel channel = new Channel(ChannelType.PUBLIC, "general", null);
        User author = new User("testuser", "test@example.com", "encoded", null);
        Message message = new Message("content", channel, author);

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        // when
        messageService.delete(messageId);

        // then
        then(messageRepository).should().findById(messageId);
        then(messageAttachmentRepository).should().deleteAllByMessageId(messageId);
        then(messageRepository).should().deleteById(messageId);
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
}
