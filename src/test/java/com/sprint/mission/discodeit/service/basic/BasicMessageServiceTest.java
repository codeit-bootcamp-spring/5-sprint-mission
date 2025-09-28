package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

    @Mock
    MessageRepository messageRepository;
    @Mock
    ChannelRepository channelRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    MessageMapper messageMapper;
    @Mock
    BinaryContentStorage binaryContentStorage;
    @Mock
    BinaryContentRepository binaryContentRepository;
    @Mock
    PageResponseMapper pageResponseMapper;

    @InjectMocks
    BasicMessageService messageService;

    @Test
    @DisplayName("메시지 생성 성공")
    void createMessageSuccess() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        Channel channel = new Channel(ChannelType.PUBLIC, "test", "desc");
        User author = new User("mike", "mike@test.com", "1234", null);
        Message message = new Message("hello", channel, author, List.of());

        MessageCreateRequest request = new MessageCreateRequest("hello", channelId, authorId);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(messageRepository.save(any(Message.class))).willReturn(message);

        MessageDto dto = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(),
                "hello", channelId, null, List.of());
        given(messageMapper.toDto(any(Message.class))).willReturn(dto);

        // when
        MessageDto result = messageService.create(request, List.of());

        // then
        assertThat(result.content()).isEqualTo("hello");
        then(messageRepository).should(times(1)).save(any(Message.class));
    }

    @Test
    @DisplayName("메시지 생성 실패 - 채널 없음")
    void createMessageFailByChannelNotFound() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest("hello", channelId, authorId);

        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> messageService.create(request, List.of()))
                .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("메시지 조회 실패 - 없음")
    void findMessageFail() {
        // given
        UUID messageId = UUID.randomUUID();
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> messageService.find(messageId))
                .isInstanceOf(MessageNotFoundException.class);
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void updateMessageSuccess() {
        // given
        UUID messageId = UUID.randomUUID();
        Channel channel = new Channel(ChannelType.PUBLIC, "test", "desc");
        User author = new User("mike", "mike@test.com", "1234", null);
        Message message = new Message("old", channel, author, List.of());

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        MessageDto dto = new MessageDto(messageId, Instant.now(), Instant.now(),
                "newContent", channel.getId(), null, List.of());
        given(messageMapper.toDto(any(Message.class))).willReturn(dto);

        // when
        MessageDto result = messageService.update(messageId, new MessageUpdateRequest("newContent"));

        // then
        assertThat(result.content()).isEqualTo("newContent");
    }

    @Test
    @DisplayName("채널별 메시지 목록 조회 성공")
    void findAllByChannelIdSuccess() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel(ChannelType.PUBLIC, "test", "desc");
        User author = new User("mike", "mike@test.com", "1234", null);
        Message message = new Message("content", channel, author, List.of());

        SliceImpl<Message> slice = new SliceImpl<>(List.of(message), PageRequest.of(0, 10), false);

        given(messageRepository.findAllByChannelIdWithAuthor(any(), any(), any())).willReturn(slice);
        given(messageMapper.toDto(any(Message.class)))
                .willReturn(new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(),
                        "content", channel.getId(), null, List.of()));
        given(pageResponseMapper.fromSlice(any(), any()))
                .willReturn(new PageResponse<>(List.of(), null, 1, false, null));

        // when
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, null, PageRequest.of(0, 10));

        // then
        assertThat(result.size()).isEqualTo(1);
    }
}