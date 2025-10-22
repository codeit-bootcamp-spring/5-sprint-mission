package com.codeit.mission.discodeit.service.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.codeit.mission.discodeit.dto.data.MessageDto;
import com.codeit.mission.discodeit.dto.data.UserDto;
import com.codeit.mission.discodeit.dto.request.MessageCreateRequest;
import com.codeit.mission.discodeit.dto.request.MessageUpdateRequest;
import com.codeit.mission.discodeit.dto.response.PageResponse;
import com.codeit.mission.discodeit.entity.Channel;
import com.codeit.mission.discodeit.entity.ChannelType;
import com.codeit.mission.discodeit.entity.Message;
import com.codeit.mission.discodeit.entity.User;
import com.codeit.mission.discodeit.mapper.MessageMapper;
import com.codeit.mission.discodeit.mapper.PageResponseMapper;
import com.codeit.mission.discodeit.repository.ChannelRepository;
import com.codeit.mission.discodeit.repository.MessageRepository;
import com.codeit.mission.discodeit.repository.UserRepository;
import com.codeit.mission.discodeit.service.basic.BasicMessageService;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

    @InjectMocks
    private BasicMessageService messageService;

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private PageResponseMapper pageResponseMapper;

    @Test
    @DisplayName("메시지 생성")
    void create() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest("Hello", channelId, authorId);
        Channel channel = new Channel(ChannelType.PUBLIC, "test-channel", "description");
        User author = new User("testuser", "test@example.com", "password123", null);
        Message message = new Message("Hello", channel, author, Collections.emptyList());

        UserDto authorDto = new UserDto(authorId, "author", "author@mail.com", null, true);
        MessageDto messageDto = new MessageDto(UUID.randomUUID(), Instant.now(), null, "Hello",
                channelId, authorDto, Collections.emptyList());

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(messageRepository.save(any(Message.class))).willReturn(message);
        given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

        // when
        MessageDto createdMessage = messageService.create(request, Collections.emptyList());

        // then
        assertThat(createdMessage).isNotNull();
        assertThat(createdMessage.content()).isEqualTo(request.content());
        then(channelRepository).should().findById(channelId);
        then(userRepository).should().findById(authorId);
        then(messageRepository).should().save(any(Message.class));
    }

    @Test
    @DisplayName("채널별 메시지 조회")
    void findAllByChannelId() {
        // given
        UUID channelId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Instant cursor = Instant.now();

        final Long TOTAL_ELEMENTS = 1L;

        Channel channel = new Channel(ChannelType.PUBLIC, "test-channel", "description");
        User author = new User("testuser", "test@example.com", "password123", null);
        Message message = new Message("Hello", channel, author, Collections.emptyList());

        UserDto authorDto = new UserDto(UUID.randomUUID(), "testuser", "test@example.com", null,
                true);
        Instant messageCreatedAt = Instant.now().minusSeconds(5);
        MessageDto messageDto = new MessageDto(UUID.randomUUID(), messageCreatedAt, null, "Hello",
                channelId, authorDto, Collections.emptyList());

        Slice<Message> messageSlice = new SliceImpl<>(List.of(message), pageable, true);

        PageResponse<MessageDto> pageResponse = new PageResponse<>(
                List.of(messageDto),
                messageDto.createdAt(),
                10,
                true,
                TOTAL_ELEMENTS
        );

        given(messageRepository.findAllByChannelIdWithAuthor(
                eq(channelId),
                any(Instant.class),
                eq(pageable)
        ))
                .willReturn(messageSlice);

        given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

        given(pageResponseMapper.fromSlice(any(Slice.class), any())).willReturn(pageResponse);

        // when
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, cursor,
                pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.hasNext()).isTrue();
        assertThat(result.content()).hasSize(1);
        assertThat(result.nextCursor()).isEqualTo(messageDto.createdAt());
        assertThat(result.totalElements()).isEqualTo(TOTAL_ELEMENTS);
    }

    @Test
    @DisplayName("메시지 업데이트")
    void update() {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("Updated content");

        Channel channel = new Channel(ChannelType.PUBLIC, "test-channel", "desc");
        User author = new User("testuser", "test@example.com", "password", null);
        Message message = new Message("Old content", channel, author, List.of());

        UserDto authorDto = new UserDto(UUID.randomUUID(), "testuser", "test@example.com", null,
                true);
        MessageDto messageDto = new MessageDto(messageId, Instant.now(), Instant.now(),
                "Updated content", UUID.randomUUID(), authorDto, List.of());

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(messageMapper.toDto(message)).willReturn(messageDto);

        // when
        MessageDto result = messageService.update(messageId, request);

        // then
        assertThat(result).isNotNull();
        then(messageRepository).should().findById(messageId);
    }

    @Test
    @DisplayName("메시지 삭제")
    void delete() {
        // given
        UUID messageId = UUID.randomUUID();
        given(messageRepository.existsById(messageId)).willReturn(true);
        willDoNothing().given(messageRepository).deleteById(messageId);

        // when
        messageService.delete(messageId);

        // then
        then(messageRepository).should().existsById(messageId);
        then(messageRepository).should().deleteById(messageId);
    }
}
