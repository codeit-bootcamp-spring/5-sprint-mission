package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private PageResponseMapper pageResponseMapper;

    @InjectMocks
    private BasicMessageService messageService;

    private UUID messageId;
    private UUID channelId;
    private UUID authorId;
    private String content;
    private User author;
    private Channel channel;
    private BinaryContent attachment;
    private BinaryContentDto attachmentDto;
    private Message message;
    private MessageDto messageDto;

    @BeforeEach
    void setUp() {
        messageId = UUID.randomUUID();
        channelId = UUID.randomUUID();
        authorId = UUID.randomUUID();
        content = "테스트 내용";

        channel = new Channel(ChannelType.PUBLIC, "testChannel", "channelDescription");
        ReflectionTestUtils.setField(channel, "id", channelId);

        author = new User("testUser", "test@email.com", "password1234", null);
        ReflectionTestUtils.setField(author, "id", authorId);

        attachment = new BinaryContent("test.png", 100L, "image/png");
        ReflectionTestUtils.setField(attachment, "id", UUID.randomUUID());
        attachmentDto = new BinaryContentDto(attachment.getId(), "test.png", 100L, "image/png");

        message = new Message(content, channel, author, List.of(attachment));
        ReflectionTestUtils.setField(message, "id", messageId);
        messageDto = new MessageDto(messageId, Instant.now(), Instant.now(), content, channelId,
            new UserDto(authorId, "testUser", "test@email.com", null, true),
            List.of(attachmentDto));

    }

    @Test
    @DisplayName("메시지 생성 성공")
    void create_success() {
        MessageCreateRequest request = new MessageCreateRequest(content, channelId, authorId);
        BinaryContentCreateRequest attachmentRequest = new BinaryContentCreateRequest("test.png",
            "image/png", new byte[100]);
        List<BinaryContentCreateRequest> attachmentRequests = List.of(attachmentRequest);

        // given
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(attachment);
        given(messageRepository.save(any(Message.class))).willReturn(message);
        given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

        // when
        MessageDto result = messageService.create(request, attachmentRequests);

        // then
        assertThat(result).isEqualTo(messageDto);
        verify(messageRepository).save(any(Message.class));
//        verify(binaryContentStorage).put();
    }

    @Test
    @DisplayName("메시지 생성 실패(존재하지 않는 채널)")
    void create_finById_ChannelNotFoundException() {
        MessageCreateRequest request = new MessageCreateRequest(content, channelId, authorId);
        BinaryContentCreateRequest attachmentRequest = new BinaryContentCreateRequest("test.png",
            "image/png", new byte[100]);
        List<BinaryContentCreateRequest> attachmentRequests = List.of(attachmentRequest);

        // given
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> messageService.create(request, attachmentRequests))
            .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("채널 아이디로 메시지 목록 검색 성공")
    void findAllByChannelId_success() {
        // given
//        given(messageRepository.findAllByChannelIdWithAuthor(channelId, Instant.now(), ))
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void update_success() {
        MessageUpdateRequest request = new MessageUpdateRequest("newContent");

        // given
        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(messageMapper.toDto(message)).willReturn(messageDto);

        // when
        MessageDto result = messageService.update(messageId, request);

        // then
        assertThat(result).isEqualTo(messageDto);
    }

    @Test
    @DisplayName("메시지 수정 실패(존재하지 않는 메시지)")
    void update_finById_MessageNotFoundException() {
        MessageUpdateRequest request = new MessageUpdateRequest("newContent");

        // given
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> messageService.update(messageId, request))
            .isInstanceOf(MessageNotFoundException.class);
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void delete_success() {
        // given
        given(messageRepository.existsById(messageId)).willReturn(true);

        // when
        messageService.delete(messageId);

        // then
        verify(messageRepository).deleteById(messageId);
    }

    @Test
    @DisplayName("메시지 삭제 실패(존재하지 않는 메시지)")
    void delete_existsById_MessageNotFoundException() {
        // given
        given(messageRepository.existsById(messageId)).willReturn(false);

        // when then
        assertThatThrownBy(() -> messageService.delete(messageId))
            .isInstanceOf(MessageNotFoundException.class);
    }
}