package com.sprint.mission.discodeit.message.application;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.binarycontent.domain.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.binarycontent.domain.exception.BinaryContentUploadException;
import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelRepository;
import com.sprint.mission.discodeit.channel.domain.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.common.presentation.dto.PaginationRequest;
import com.sprint.mission.discodeit.common.presentation.dto.PaginationResponse;
import com.sprint.mission.discodeit.message.domain.Message;
import com.sprint.mission.discodeit.message.domain.MessageRepository;
import com.sprint.mission.discodeit.message.domain.attachment.MessageAttachment;
import com.sprint.mission.discodeit.message.domain.attachment.MessageAttachmentRepository;
import com.sprint.mission.discodeit.message.domain.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.message.domain.event.MessageDeletedEvent;
import com.sprint.mission.discodeit.message.domain.exception.EmptyMessageContentException;
import com.sprint.mission.discodeit.message.domain.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.message.presentation.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.message.presentation.dto.MessageDto;
import com.sprint.mission.discodeit.message.presentation.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.user.application.UserMapper;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import com.sprint.mission.discodeit.user.domain.exception.UserNotFoundException;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageService 단위 테스트")
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageAttachmentRepository messageAttachmentRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MessageService messageService;

    private static final UUID CHANNEL_ID = UUID.randomUUID();
    private static final UUID AUTHOR_ID = UUID.randomUUID();
    private static final UUID MESSAGE_ID = UUID.randomUUID();

    @Nested
    @DisplayName("create")
    class CreateTest {

        @ParameterizedTest(name = "[{index}] attachments: {0}")
        @MethodSource("provideEmptyAttachments")
        @DisplayName("텍스트만 입력 시 메시지 생성 성공")
        void create_withTextOnly_success(List<MultipartFile> attachments) {
            // given
            MessageCreateRequest request = new MessageCreateRequest("Hello", CHANNEL_ID, AUTHOR_ID);

            Channel channel = mock(Channel.class);
            User author = mock(User.class);
            Message savedMessage = mock(Message.class);
            MessageDto expectedDto = createMessageDto("Hello");

            given(channelRepository.findById(CHANNEL_ID)).willReturn(Optional.of(channel));
            given(userRepository.findById(AUTHOR_ID)).willReturn(Optional.of(author));
            given(messageRepository.save(any(Message.class))).willReturn(savedMessage);
            given(savedMessage.getId()).willReturn(MESSAGE_ID);
            given(messageMapper.toDto(eq(savedMessage), anyList())).willReturn(expectedDto);

            // when
            MessageDto result = messageService.create(request, attachments);

            // then
            assertThat(result).isEqualTo(expectedDto);
            then(eventPublisher).should().publishEvent(any(MessageCreatedEvent.class));
        }

        private static Stream<Arguments> provideEmptyAttachments() {
            return Stream.of(
                Arguments.of((Object) null),
                Arguments.of(Collections.emptyList())
            );
        }

        @Test
        @DisplayName("첨부파일만 입력 시 메시지 생성 성공")
        void create_withAttachmentOnly_success() {
            // given
            MessageCreateRequest request = new MessageCreateRequest(null, CHANNEL_ID, AUTHOR_ID);
            MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "content".getBytes());

            Channel channel = mock(Channel.class);
            User author = mock(User.class);
            Message savedMessage = mock(Message.class);
            MessageDto expectedDto = createMessageDto(null);

            given(channelRepository.findById(CHANNEL_ID)).willReturn(Optional.of(channel));
            given(userRepository.findById(AUTHOR_ID)).willReturn(Optional.of(author));
            given(messageRepository.save(any(Message.class))).willReturn(savedMessage);
            given(savedMessage.getId()).willReturn(MESSAGE_ID);
            given(messageMapper.toDto(eq(savedMessage), anyList())).willReturn(expectedDto);

            // when
            MessageDto result = messageService.create(request, List.of(file));

            // then
            assertThat(result).isEqualTo(expectedDto);
            then(binaryContentRepository).should().saveAll(anyList());
            then(messageAttachmentRepository).should().saveAll(anyList());
            then(eventPublisher).should().publishEvent(any(MessageCreatedEvent.class));
            then(eventPublisher).should().publishEvent(any(BinaryContentCreatedEvent.class));
        }

        @Test
        @DisplayName("텍스트와 첨부파일 입력 시 메시지 생성 성공")
        void create_withTextAndAttachment_success() {
            // given
            MessageCreateRequest request = new MessageCreateRequest("Hello", CHANNEL_ID, AUTHOR_ID);
            MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "content".getBytes());

            Channel channel = mock(Channel.class);
            User author = mock(User.class);
            Message savedMessage = mock(Message.class);
            MessageDto expectedDto = createMessageDto("Hello");

            given(channelRepository.findById(CHANNEL_ID)).willReturn(Optional.of(channel));
            given(userRepository.findById(AUTHOR_ID)).willReturn(Optional.of(author));
            given(messageRepository.save(any(Message.class))).willReturn(savedMessage);
            given(savedMessage.getId()).willReturn(MESSAGE_ID);
            given(messageMapper.toDto(eq(savedMessage), anyList())).willReturn(expectedDto);

            // when
            MessageDto result = messageService.create(request, List.of(file));

            // then
            assertThat(result).isEqualTo(expectedDto);
            then(binaryContentRepository).should().saveAll(anyList());
            then(messageAttachmentRepository).should().saveAll(anyList());
        }

        @Test
        @DisplayName("채널 없음 시 ChannelNotFoundException 발생")
        void create_withNonExistentChannel_throwsException() {
            // given
            MessageCreateRequest request = new MessageCreateRequest("Hello", CHANNEL_ID, AUTHOR_ID);
            given(channelRepository.findById(CHANNEL_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> messageService.create(request, null))
                .isInstanceOf(ChannelNotFoundException.class);
        }

        @Test
        @DisplayName("작성자 없음 시 UserNotFoundException 발생")
        void create_withNonExistentAuthor_throwsException() {
            // given
            MessageCreateRequest request = new MessageCreateRequest("Hello", CHANNEL_ID, AUTHOR_ID);
            Channel channel = mock(Channel.class);

            given(channelRepository.findById(CHANNEL_ID)).willReturn(Optional.of(channel));
            given(userRepository.findById(AUTHOR_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> messageService.create(request, null))
                .isInstanceOf(UserNotFoundException.class);
        }

        @ParameterizedTest(name = "[{index}] Content: \"{0}\", Attachments: {1} -> 예외 발생")
        @MethodSource("provideInvalidCombinations")
        @DisplayName("내용과 첨부파일 모두 비어있음 시 EmptyMessageContentException 발생")
        void create_withEmptyContentAndNoAttachment_throwsException(
            String content, List<MultipartFile> attachments
        ) {
            // given
            MessageCreateRequest request = new MessageCreateRequest(content, CHANNEL_ID, AUTHOR_ID);
            Channel channel = mock(Channel.class);
            User author = mock(User.class);

            given(channelRepository.findById(CHANNEL_ID)).willReturn(Optional.of(channel));
            given(userRepository.findById(AUTHOR_ID)).willReturn(Optional.of(author));

            // when & then
            assertThatThrownBy(() -> messageService.create(request, attachments))
                .isInstanceOf(EmptyMessageContentException.class);
        }

        private static Stream<Arguments> provideInvalidCombinations() {
            return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, Collections.emptyList()),
                Arguments.of("   ", null),
                Arguments.of("   ", Collections.emptyList())
            );
        }

        @Test
        @DisplayName("첨부파일 읽기 실패 시 BinaryContentUploadException 발생")
        void create_withAttachmentReadFailure_throwsBinaryContentUploadException() throws IOException {
            // given
            MessageCreateRequest request = new MessageCreateRequest("Hello", CHANNEL_ID, AUTHOR_ID);

            MultipartFile failingFile = mock(MultipartFile.class);
            given(failingFile.getBytes()).willThrow(new IOException("File read error"));

            Channel channel = mock(Channel.class);
            User author = mock(User.class);
            Message savedMessage = mock(Message.class);

            given(channelRepository.findById(CHANNEL_ID)).willReturn(Optional.of(channel));
            given(userRepository.findById(AUTHOR_ID)).willReturn(Optional.of(author));
            given(messageRepository.save(any(Message.class))).willReturn(savedMessage);

            // when & then
            assertThatThrownBy(() -> messageService.create(request, List.of(failingFile)))
                .isInstanceOf(BinaryContentUploadException.class)
                .hasCauseInstanceOf(IOException.class);
        }
    }

    @Nested
    @DisplayName("findAllByChannelId")
    class FindAllByChannelIdTest {

        @Test
        @DisplayName("유효한 채널 ID 조회 시 메시지 목록 반환")
        void findAllByChannelId_success() {
            // given
            PaginationRequest paginationRequest = new PaginationRequest(null, null, null);
            PageRequest pageRequest = paginationRequest.toPageRequest();

            Message message = mock(Message.class);
            User author = mock(User.class);
            UserDto authorDto = createUserDto();
            MessageDto messageDto = createMessageDto("Hello");

            given(message.getId()).willReturn(MESSAGE_ID);
            given(message.getAuthor()).willReturn(author);
            given(author.getId()).willReturn(AUTHOR_ID);

            SliceImpl<Message> slice = new SliceImpl<>(List.of(message), pageRequest, false);
            given(messageRepository.findSliceWithAuthorAndProfileByChannelIdAndCreatedAtBefore(
                eq(CHANNEL_ID), any(Instant.class), any(PageRequest.class))).willReturn(slice);
            given(messageAttachmentRepository.findAllWithAttachmentByMessageIdInOrderByOrderIndexAsc(anyList()))
                .willReturn(List.of());
            given(userMapper.toDto(author)).willReturn(authorDto);
            given(messageMapper.toDtoWithAuthorDto(eq(message), eq(authorDto), anyList()))
                .willReturn(messageDto);

            // when
            PaginationResponse<MessageDto> result = messageService.findAllByChannelId(
                CHANNEL_ID, null, paginationRequest);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.hasNext()).isFalse();
        }

        @Test
        @DisplayName("메시지 없음 시 빈 응답 반환")
        void findAllByChannelId_whenEmpty_returnsEmptyResponse() {
            // given
            PaginationRequest paginationRequest = new PaginationRequest(null, 10, List.of("createdAt", "desc"));

            SliceImpl<Message> emptySlice = new SliceImpl<>(List.of());
            given(messageRepository.findSliceWithAuthorAndProfileByChannelIdAndCreatedAtBefore(
                eq(CHANNEL_ID), any(Instant.class), any(PageRequest.class))).willReturn(emptySlice);

            // when
            PaginationResponse<MessageDto> result = messageService.findAllByChannelId(
                CHANNEL_ID, null, paginationRequest);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.hasNext()).isFalse();
            assertThat(result.nextCursor()).isNull();
        }

        @Test
        @DisplayName("커서 입력 시 해당 시점 이전 메시지 조회")
        void findAllByChannelId_withCursor_returnsMessagesBeforeCursor() {
            // given
            Instant cursor = Instant.now().minusSeconds(60);
            PaginationRequest paginationRequest = new PaginationRequest(0, null, List.of("createdAt", "desc"));

            SliceImpl<Message> emptySlice = new SliceImpl<>(List.of());
            given(messageRepository.findSliceWithAuthorAndProfileByChannelIdAndCreatedAtBefore(
                eq(CHANNEL_ID), eq(cursor), any(PageRequest.class))).willReturn(emptySlice);

            // when
            messageService.findAllByChannelId(CHANNEL_ID, cursor, paginationRequest);

            // then
            then(messageRepository).should().findSliceWithAuthorAndProfileByChannelIdAndCreatedAtBefore(
                eq(CHANNEL_ID), eq(cursor), any(PageRequest.class));
        }

        @Test
        @DisplayName("다음 페이지 있음 시 hasNext true 및 nextCursor 설정")
        void findAllByChannelId_whenHasNext_returnsNextCursor() {
            // given
            PaginationRequest paginationRequest = new PaginationRequest(0, 10, null);
            PageRequest pageRequest = paginationRequest.toPageRequest();

            Message message = mock(Message.class);
            User author = mock(User.class);
            Instant messageCreatedAt = Instant.now().minusSeconds(30);

            given(message.getId()).willReturn(MESSAGE_ID);
            given(message.getAuthor()).willReturn(author);
            given(author.getId()).willReturn(AUTHOR_ID);

            UserDto authorDto = createUserDto();
            MessageDto messageDto = new MessageDto(
                MESSAGE_ID, messageCreatedAt, messageCreatedAt, "Hello", CHANNEL_ID, authorDto, List.of());

            SliceImpl<Message> slice = new SliceImpl<>(List.of(message), pageRequest, true);
            given(messageRepository.findSliceWithAuthorAndProfileByChannelIdAndCreatedAtBefore(
                eq(CHANNEL_ID), any(Instant.class), any(PageRequest.class))).willReturn(slice);
            given(messageAttachmentRepository.findAllWithAttachmentByMessageIdInOrderByOrderIndexAsc(anyList()))
                .willReturn(List.of());
            given(userMapper.toDto(author)).willReturn(authorDto);
            given(messageMapper.toDtoWithAuthorDto(eq(message), eq(authorDto), anyList()))
                .willReturn(messageDto);

            // when
            PaginationResponse<MessageDto> result = messageService.findAllByChannelId(
                CHANNEL_ID, null, paginationRequest);

            // then
            assertThat(result.hasNext()).isTrue();
            assertThat(result.nextCursor()).isEqualTo(messageCreatedAt);
        }

        @Test
        @DisplayName("첨부파일 있음 시 올바른 메시지에 매핑하여 반환")
        void findAllByChannelId_withAttachments_mapsCorrectly() {
            // given
            PaginationRequest paginationRequest = new PaginationRequest(0, 10, List.of("createdAt", "invalid"));
            PageRequest pageRequest = paginationRequest.toPageRequest();

            Message msg1 = mock(Message.class);
            Message msg2 = mock(Message.class);
            given(msg1.getId()).willReturn(UUID.randomUUID());
            given(msg2.getId()).willReturn(UUID.randomUUID());

            BinaryContent content = mock(BinaryContent.class);
            MessageAttachment attachment = mock(MessageAttachment.class);
            given(attachment.getMessage()).willReturn(msg1);
            given(attachment.getAttachment()).willReturn(content);

            SliceImpl<Message> slice = new SliceImpl<>(List.of(msg1, msg2), pageRequest, false);
            given(messageRepository.findSliceWithAuthorAndProfileByChannelIdAndCreatedAtBefore(
                any(), any(), any())).willReturn(slice);
            given(messageAttachmentRepository.findAllWithAttachmentByMessageIdInOrderByOrderIndexAsc(anyList()))
                .willReturn(List.of(attachment));

            given(messageMapper.toDtoWithAuthorDto(eq(msg1), any(), eq(List.of(content))))
                .willReturn(createMessageDto("msg1"));
            given(messageMapper.toDtoWithAuthorDto(eq(msg2), any(), eq(List.of())))
                .willReturn(createMessageDto("msg2"));

            // when
            messageService.findAllByChannelId(CHANNEL_ID, null, paginationRequest);

            // then
            verify(messageMapper).toDtoWithAuthorDto(eq(msg1), any(), eq(List.of(content)));
            verify(messageMapper).toDtoWithAuthorDto(eq(msg2), any(), eq(List.of()));
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTest {

        @Test
        @DisplayName("유효한 요청 시 메시지 내용 수정 성공")
        void update_success() {
            // given
            MessageUpdateRequest request = new MessageUpdateRequest("Updated content");

            Message message = mock(Message.class);
            MessageDto expectedDto = createMessageDto("Updated content");

            given(messageRepository.findById(MESSAGE_ID)).willReturn(Optional.of(message));
            given(messageAttachmentRepository.findAllWithAttachmentByMessageIdOrderByOrderIndexAsc(MESSAGE_ID))
                .willReturn(List.of());
            given(messageMapper.toDto(eq(message), anyList())).willReturn(expectedDto);

            // when
            MessageDto result = messageService.update(MESSAGE_ID, request);

            // then
            assertThat(result).isEqualTo(expectedDto);
            then(message).should().update("Updated content");
        }

        @Test
        @DisplayName("메시지 없음 시 MessageNotFoundException 발생")
        void update_withNonExistentMessage_throwsException() {
            // given
            MessageUpdateRequest request = new MessageUpdateRequest("Updated content");
            given(messageRepository.findById(MESSAGE_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> messageService.update(MESSAGE_ID, request))
                .isInstanceOf(MessageNotFoundException.class);
        }

        @Test
        @DisplayName("공백 내용 및 첨부파일 없음 시 EmptyMessageContentException 발생")
        void update_withBlankContentAndNoAttachment_throwsException() {
            // given
            MessageUpdateRequest request = new MessageUpdateRequest("   ");

            Message message = mock(Message.class);
            given(messageRepository.findById(MESSAGE_ID)).willReturn(Optional.of(message));
            given(messageAttachmentRepository.findAllWithAttachmentByMessageIdOrderByOrderIndexAsc(MESSAGE_ID))
                .willReturn(List.of());

            // when & then
            assertThatThrownBy(() -> messageService.update(MESSAGE_ID, request))
                .isInstanceOf(EmptyMessageContentException.class);
        }

        @Test
        @DisplayName("공백 내용이지만 첨부파일 있음 시 수정 성공")
        void update_withBlankContentButHasAttachment_success() {
            // given
            MessageUpdateRequest request = new MessageUpdateRequest("   ");

            Message message = mock(Message.class);
            MessageAttachment attachment = mock(MessageAttachment.class);
            BinaryContent binaryContent = mock(BinaryContent.class);
            MessageDto expectedDto = createMessageDto("");

            given(messageRepository.findById(MESSAGE_ID)).willReturn(Optional.of(message));
            given(messageAttachmentRepository.findAllWithAttachmentByMessageIdOrderByOrderIndexAsc(MESSAGE_ID))
                .willReturn(List.of(attachment));
            given(attachment.getAttachment()).willReturn(binaryContent);
            given(messageMapper.toDto(eq(message), anyList())).willReturn(expectedDto);

            // when
            MessageDto result = messageService.update(MESSAGE_ID, request);

            // then
            assertThat(result).isEqualTo(expectedDto);
            then(message).should().update("");
        }

        @Test
        @DisplayName("newContent가 null 시 update 호출하지 않음")
        void update_withNullContent_doesNotUpdateMessage() {
            // given
            MessageUpdateRequest request = new MessageUpdateRequest(null);

            Message message = mock(Message.class);
            MessageDto expectedDto = createMessageDto("Original content");

            given(messageRepository.findById(MESSAGE_ID)).willReturn(Optional.of(message));
            given(messageAttachmentRepository.findAllWithAttachmentByMessageIdOrderByOrderIndexAsc(MESSAGE_ID))
                .willReturn(List.of());
            given(messageMapper.toDto(eq(message), anyList())).willReturn(expectedDto);

            // when
            messageService.update(MESSAGE_ID, request);

            // then
            then(message).should(never()).update(any());
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteByIdTest {

        @Test
        @DisplayName("존재하는 메시지 삭제 시 삭제 성공 및 이벤트 발행")
        void deleteById_success() {
            // given
            Message message = mock(Message.class);
            given(messageRepository.findById(MESSAGE_ID)).willReturn(Optional.of(message));

            // when
            messageService.deleteById(MESSAGE_ID);

            // then
            then(messageRepository).should().deleteById(MESSAGE_ID);

            ArgumentCaptor<MessageDeletedEvent> eventCaptor = ArgumentCaptor.forClass(MessageDeletedEvent.class);
            then(eventPublisher).should().publishEvent(eventCaptor.capture());
            assertThat(eventCaptor.getValue().messageId()).isEqualTo(MESSAGE_ID);
        }

        @Test
        @DisplayName("메시지 없음 시 MessageNotFoundException 발생")
        void deleteById_withNonExistentMessage_throwsException() {
            // given
            given(messageRepository.findById(MESSAGE_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> messageService.deleteById(MESSAGE_ID))
                .isInstanceOf(MessageNotFoundException.class);

            then(messageRepository).should(never()).deleteById(any());
            then(eventPublisher).should(never()).publishEvent(any(MessageDeletedEvent.class));
        }
    }

    @Nested
    @DisplayName("isAuthor")
    class IsAuthorTest {

        @Test
        @DisplayName("작성자 일치 시 true 반환")
        void isAuthor_whenAuthorMatches_returnsTrue() {
            // given
            given(messageRepository.existsByIdAndAuthorId(MESSAGE_ID, AUTHOR_ID)).willReturn(true);

            // when
            boolean result = messageService.isAuthor(MESSAGE_ID, AUTHOR_ID);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("작성자 불일치 시 false 반환")
        void isAuthor_whenAuthorDoesNotMatch_returnsFalse() {
            // given
            UUID otherUserId = UUID.randomUUID();
            given(messageRepository.existsByIdAndAuthorId(MESSAGE_ID, otherUserId)).willReturn(false);

            // when
            boolean result = messageService.isAuthor(MESSAGE_ID, otherUserId);

            // then
            assertThat(result).isFalse();
        }
    }

    private MessageDto createMessageDto(String content) {
        return new MessageDto(
            MessageServiceTest.MESSAGE_ID,
            Instant.now(),
            Instant.now(),
            content,
            CHANNEL_ID,
            createUserDto(),
            List.of()
        );
    }

    private UserDto createUserDto() {
        return new UserDto(MessageServiceTest.AUTHOR_ID, "testuser", "test@test.com", null, true, Role.USER);
    }
}
