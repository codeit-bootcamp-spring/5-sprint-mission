package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageDeleteResponse;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.message.UnauthorizedMessageAccessException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private PageResponseMapper pageResponseMapper;

    @InjectMocks
    private BasicMessageService messageService;

    @Test
    @DisplayName("메시지 생성 첨부파일 미포함 성공")
    void create_success_withoutAttachments() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        MessageCreateRequest request = MessageCreateRequest.builder()
                .authorId(userId)
                .channelId(channelId)
                .content("테스트 메시지")
                .attachments(List.of())
                .build();

        User author = User.builder()
                .id(userId)
                .username("test")
                .email("test@example.com")
                .password("password")
                .build();

        Channel channel = Channel.builder()
                .id(channelId)
                .name("testChannel")
                .description("General channel")
                .type(ChannelType.PUBLIC)
                .build();

        Message savedMessage = Message.builder()
                .id(UUID.randomUUID())
                .author(author)
                .channel(channel)
                .content("테스트 메시지")
                .attachments(List.of())
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(author));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(messageRepository.save(any(Message.class))).willReturn(savedMessage);

        // when
        MessageResponse result = messageService.create(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("테스트 메시지");
        assertThat(result.getAuthor().getUsername()).isEqualTo("test");

        then(userRepository).should().findById(userId);
        then(channelRepository).should().findById(channelId);
        then(messageRepository).should().save(any(Message.class));
        then(binaryContentRepository).should(never()).save(any(BinaryContent.class));
    }

    @Test
    @DisplayName("메시지 생성 첨부파일 포함 성공")
    void create_success_withAttachments() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        byte[] fileData = "test-file-data".getBytes();
        BinaryContentCreateRequest attachment = BinaryContentCreateRequest.builder()
                .fileName("test.jpg")
                .contentType("image/jpeg")
                .size((long) fileData.length)
                .bytes(fileData)
                .build();

        MessageCreateRequest request = MessageCreateRequest.builder()
                .authorId(userId)
                .channelId(channelId)
                .content("첨부파일")
                .attachments(List.of(attachment))
                .build();

        User testUser = User.builder()
                .id(userId)
                .username("test")
                .build();

        Channel testChannel = Channel.builder()
                .id(channelId)
                .name("testChannel")
                .type(ChannelType.PUBLIC)
                .build();

        BinaryContent savedBinaryContent = BinaryContent.builder()
                .id(UUID.randomUUID())
                .fileName("test.jpg")
                .contentType("image/jpeg")
                .size((long) fileData.length)
                .build();

        Message savedMessage = Message.builder()
                .id(UUID.randomUUID())
                .author(testUser)
                .channel(testChannel)
                .content("첨부파일")
                .attachments(List.of())
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(testChannel));
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(savedBinaryContent);
        given(binaryContentStorage.put(any(UUID.class), any(byte[].class))).willReturn(savedBinaryContent.getId());
        given(messageRepository.save(any(Message.class))).willReturn(savedMessage);

        // when
        MessageResponse result = messageService.create(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("첨부파일");

        then(userRepository).should().findById(userId);
        then(channelRepository).should().findById(channelId);
        then(binaryContentRepository).should().save(any(BinaryContent.class));
        then(binaryContentStorage).should().put(any(UUID.class), any(byte[].class));
        then(messageRepository).should().save(any(Message.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 메시지 생성 실패")
    void create_fail_userNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        MessageCreateRequest request = MessageCreateRequest.builder()
                .authorId(userId)
                .channelId(channelId)
                .content("테스트 메시지")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> messageService.create(request))
                .isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().findById(userId);
        then(channelRepository).should(never()).findById(any(UUID.class));
        then(messageRepository).should(never()).save(any(Message.class));
    }

    @Test
    @DisplayName("존재하지 않는 채널로 메시지 생성 실패")
    void create_fail_channelNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        MessageCreateRequest request = MessageCreateRequest.builder()
                .authorId(userId)
                .channelId(channelId)
                .content("테스트 메시지")
                .build();

        User testUser = User.builder()
                .id(userId)
                .username("test")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> messageService.create(request))
                .isInstanceOf(ChannelNotFoundException.class);

        then(userRepository).should().findById(userId);
        then(channelRepository).should().findById(channelId);
        then(messageRepository).should(never()).save(any(Message.class));
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void update_success() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        MessageUpdateRequest request = MessageUpdateRequest.builder()
                .authorId(userId)
                .content("수정된 메시지")
                .attachmentIdsToRemove(List.of())
                .attachmentsToAdd(List.of())
                .build();

        User testUser = User.builder()
                .id(userId)
                .username("test")
                .build();

        Channel testChannel = Channel.builder()
                .id(UUID.randomUUID())
                .name("testChannel")
                .build();

        Message existingMessage = Message.builder()
                .id(messageId)
                .author(testUser)
                .channel(testChannel)
                .content("원본 메시지")
                .attachments(List.of())
                .build();

        given(messageRepository.findById(messageId)).willReturn(Optional.of(existingMessage));
        given(messageRepository.save(any(Message.class))).willReturn(existingMessage);

        // when
        MessageResponse result = messageService.updateMessage(messageId, request);

        // then
        assertThat(result).isNotNull();

        then(messageRepository).should().findById(messageId);
        then(messageRepository).should().save(any(Message.class));
    }

    @Test
    @DisplayName("권한 없는 사용자로 메시지 수정 실패")
    void update_fail_unauthorized() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        MessageUpdateRequest request = MessageUpdateRequest.builder()
                .authorId(otherUserId)
                .content("수정 시도")
                .build();

        User originalAuthor = User.builder()
                .id(authorId)
                .username("original")
                .build();

        Message existingMessage = Message.builder()
                .id(messageId)
                .author(originalAuthor)
                .content("원본 메시지")
                .attachments(List.of())
                .build();

        given(messageRepository.findById(messageId)).willReturn(Optional.of(existingMessage));

        // when
        // then
        assertThatThrownBy(() -> messageService.updateMessage(messageId, request))
                .isInstanceOf(UnauthorizedMessageAccessException.class);

        then(messageRepository).should().findById(messageId);
        then(messageRepository).should(never()).save(any(Message.class));
    }

    @Test
    @DisplayName("존재하지 않는 메시지 수정 실패")
    void update_fail_messageNotFound() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        MessageUpdateRequest request = MessageUpdateRequest.builder()
                .authorId(userId)
                .content("수정 시도")
                .build();

        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> messageService.updateMessage(messageId, request))
                .isInstanceOf(MessageNotFoundException.class);

        then(messageRepository).should().findById(messageId);
        then(messageRepository).should(never()).save(any(Message.class));
    }

    @Test
    @DisplayName("메시지 삭제 성공 - 첨부파일 없음")
    void delete_success_withoutAttachments() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User testUser = User.builder()
                .id(userId)
                .username("test")
                .build();

        Channel testChannel = Channel.builder()
                .id(UUID.randomUUID())
                .name("testChannel")
                .build();

        Message existingMessage = Message.builder()
                .id(messageId)
                .author(testUser)
                .channel(testChannel)
                .content("삭제할 메시지")
                .attachments(List.of())
                .build();

        given(messageRepository.findById(messageId)).willReturn(Optional.of(existingMessage));

        // when
        MessageDeleteResponse result = messageService.deleteMessage(messageId, userId);

        // then
        assertThat(result).isNotNull();

        then(messageRepository).should().findById(messageId);
        then(messageRepository).should().deleteById(messageId);
        then(binaryContentRepository).should(never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("메시지 삭제 성공 - 첨부파일 포함")
    void delete_success_withAttachments() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID attachmentId = UUID.randomUUID();

        User testUser = User.builder()
                .id(userId)
                .username("test")
                .build();

        BinaryContent attachment = BinaryContent.builder()
                .id(attachmentId)
                .fileName("test.jpg")
                .contentType("image/jpeg")
                .size(1024L)
                .build();

        Channel testChannel = Channel.builder()
                .id(UUID.randomUUID())
                .name("testChannel")
                .build();

        Message existingMessage = Message.builder()
                .id(messageId)
                .author(testUser)
                .channel(testChannel)
                .content("삭제할 메시지")
                .attachments(List.of(attachment))
                .build();

        given(messageRepository.findById(messageId)).willReturn(Optional.of(existingMessage));

        // when
        MessageDeleteResponse result = messageService.deleteMessage(messageId, userId);

        // then
        assertThat(result).isNotNull();

        then(messageRepository).should().findById(messageId);
        then(binaryContentRepository).should().deleteById(attachmentId);
        then(messageRepository).should().deleteById(messageId);
    }

    @Test
    @DisplayName("권한 없는 사용자로 메시지 삭제 실패")
    void delete_fail_unauthorized() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        User originalAuthor = User.builder()
                .id(authorId)
                .username("original")
                .build();

        Channel testChannel = Channel.builder()
                .id(UUID.randomUUID())
                .name("testChannel")
                .build();

        Message existingMessage = Message.builder()
                .id(messageId)
                .author(originalAuthor)
                .channel(testChannel)
                .content("삭제 시도할 메시지")
                .attachments(List.of())
                .build();

        given(messageRepository.findById(messageId)).willReturn(Optional.of(existingMessage));

        // when
        // then
        assertThatThrownBy(() -> messageService.deleteMessage(messageId, otherUserId))
                .isInstanceOf(UnauthorizedMessageAccessException.class);

        then(messageRepository).should().findById(messageId);
        then(messageRepository).should(never()).deleteById(messageId);
    }

    @Test
    @DisplayName("존재하지 않는 메시지 삭제 실패")
    void delete_fail_messageNotFound() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> messageService.deleteMessage(messageId, userId))
                .isInstanceOf(MessageNotFoundException.class);

        then(messageRepository).should().findById(messageId);
        then(messageRepository).should(never()).deleteById(messageId);
    }

    @Test
    @DisplayName("채널별 메시지 조회 성공")
    void findByChannelId_success() {
        // given
        UUID channelId = UUID.randomUUID();

        User testUser = User.builder()
                .id(UUID.randomUUID())
                .username("test")
                .build();

        Channel testChannel = Channel.builder()
                .id(channelId)
                .name("testChannel")
                .build();

        Message testMessage = Message.builder()
                .id(UUID.randomUUID())
                .author(testUser)
                .channel(testChannel)
                .content("테스트 메시지")
                .attachments(List.of())
                .build();

        given(messageRepository.findByChannelId(channelId)).willReturn(List.of(testMessage));

        // when
        List<MessageResponse> result = messageService.findMessagesByChannelId(channelId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("테스트 메시지");
        assertThat(result.get(0).getAuthor().getUsername()).isEqualTo("test");

        then(messageRepository).should().findByChannelId(channelId);
    }

    @Test
    @DisplayName("채널별 메시지 조회")
    void findByChannelId_empty() {
        // given
        UUID channelId = UUID.randomUUID();
        given(messageRepository.findByChannelId(channelId)).willReturn(List.of());

        // when
        List<MessageResponse> result = messageService.findMessagesByChannelId(channelId);

        // then
        assertThat(result).isEmpty();

        then(messageRepository).should().findByChannelId(channelId);
    }
}