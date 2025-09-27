package com.sprint.mission.discodeit.unit.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
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
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

@ExtendWith(MockitoExtension.class)
public class MessageServiceUnitTest {

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

	@Test
	@DisplayName("메시지 생성 - 성공")
	void createMessage_success() {
		String requestContent = "Content";
		UUID channelId = UUID.randomUUID();
		UUID authorId = UUID.randomUUID();
		UUID messageId = UUID.randomUUID();
		UUID binaryId1 = UUID.randomUUID();
		UUID binaryId2 = UUID.randomUUID();

		Channel publicChannel = new Channel(ChannelType.PUBLIC, "channelName", "channelDescription");
		User authorUser = new User("authorName", "authorEmail", "authorPassword", null);
		ReflectionTestUtils.setField(publicChannel, "id", channelId);
		ReflectionTestUtils.setField(authorUser, "id", authorId);

		BinaryContentCreateRequest binaryContentCreateRequest1 = new BinaryContentCreateRequest("testFileName1",
			"testContentType1", "testContent1".getBytes());
		BinaryContentCreateRequest binaryContentCreateRequest2 = new BinaryContentCreateRequest("testFileName2",
			"testContentType2", "testContent2".getBytes());
		List<BinaryContentCreateRequest> binaryRequests = List.of(binaryContentCreateRequest1,
			binaryContentCreateRequest2);
		MessageCreateRequest messageCreateRequest = new MessageCreateRequest(requestContent, channelId, authorId);

		Deque<UUID> ids = new ArrayDeque<>(List.of(binaryId1, binaryId2));
		given(binaryContentRepository.save(any(BinaryContent.class)))
			.willAnswer(inv -> {
				BinaryContent arg = inv.getArgument(0);
				ReflectionTestUtils.setField(arg, "id", ids.removeFirst());
				return arg;
			});

		given(channelRepository.findById(channelId)).willReturn(Optional.of(publicChannel));
		given(userRepository.findById(authorId)).willReturn(Optional.of(authorUser));

		given(binaryContentStorage.put(any(UUID.class), any(byte[].class)))
			.willAnswer(inv -> inv.getArgument(0));

		given(messageRepository.save(any(Message.class)))
			.willAnswer(inv -> {
				Message m = inv.getArgument(0);
				ReflectionTestUtils.setField(m, "id", messageId);
				ReflectionTestUtils.setField(m, "createdAt", Instant.now());
				ReflectionTestUtils.setField(m, "updatedAt", Instant.now());
				return m;
			});

		given(messageMapper.toDto(any(Message.class)))
			.willAnswer(inv -> {
				Message m = inv.getArgument(0);
				UserDto userDto = new UserDto(authorId, authorUser.getUsername(), authorUser.getEmail(), null, true);
				List<BinaryContentDto> attachments = m.getAttachments().stream()
					.map(b -> new BinaryContentDto(b.getId(), b.getFileName(), b.getSize(), b.getContentType()))
					.toList();
				return new MessageDto(
					m.getId(),
					m.getCreatedAt(),
					m.getUpdatedAt(),
					m.getContent(),
					m.getChannel().getId(),
					userDto,
					attachments
				);
			});

		MessageDto result = messageService.create(messageCreateRequest, binaryRequests);

		assertThat(result).isNotNull();
		assertThat(result.content()).isEqualTo(requestContent);
		assertThat(result.channelId()).isEqualTo(channelId);
		assertThat(result.author().id()).isEqualTo(authorId);
		assertThat(result.attachments()).hasSize(2);
		assertThat(result.attachments().stream().map(BinaryContentDto::id)).containsExactlyInAnyOrder(binaryId1,
			binaryId2);

		verify(channelRepository).findById(channelId);
		verify(userRepository).findById(authorId);
		verify(binaryContentRepository, times(2)).save(any(BinaryContent.class));
		verify(messageRepository).save(any(Message.class));
		verify(messageMapper).toDto(any(Message.class));
	}

	@Test
	@DisplayName("메시지 생성 - 실패 - 채널 찾기 실패")
	public void createMessage_fail() {
		UUID channelId = UUID.randomUUID();
		UUID authorId = UUID.randomUUID();
		MessageCreateRequest messageCreateRequest = new MessageCreateRequest("testContent", channelId, authorId);

		BinaryContentCreateRequest binaryContentCreateRequest1 = new BinaryContentCreateRequest("testFileName",
			"testContentType", "testContent1".getBytes());
		BinaryContentCreateRequest binaryContentCreateRequest2 = new BinaryContentCreateRequest("testFileName2",
			"testContentType2", "testContent2".getBytes());
		List<BinaryContentCreateRequest> binaryContentCreateRequest = List.of(binaryContentCreateRequest1,
			binaryContentCreateRequest2);

		given(channelRepository.findById(channelId)).willReturn(Optional.empty());
		assertThatThrownBy(() -> messageService.create(messageCreateRequest, binaryContentCreateRequest))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@DisplayName("메시지 수정 - 성공")
	public void updateMessage_success() {
		UUID messageId = UUID.randomUUID();
		MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest("newContent");

		Message existingMessage = new Message("existingContent", null, null, null);
		given(messageRepository.findById(messageId)).willReturn(Optional.of(existingMessage));

		MessageDto messageDto = new MessageDto(messageId, null, null, messageUpdateRequest.newContent(), null, null,
			null);
		given(messageMapper.toDto(existingMessage)).willReturn(messageDto);
		//
		MessageDto updatedMessage = messageService.update(messageId, messageUpdateRequest);
		//
		assertThat(updatedMessage).isNotNull();
		assertThat(updatedMessage.content()).isEqualTo(messageUpdateRequest.newContent());

		verify(messageRepository).findById(messageId);
		verify(messageMapper).toDto(existingMessage);
	}

	@Test
	@DisplayName("메시지 수정 - 실패 - 메세지를 못찾음")
	public void updateMessage_fail() {
		//
		UUID messageId = UUID.randomUUID();
		MessageUpdateRequest request = new MessageUpdateRequest("newContent");

		given(messageRepository.findById(messageId)).willReturn(Optional.empty());

		//
		assertThatThrownBy(() -> messageService.update(messageId, request))
			.isInstanceOf(MessageNotFoundException.class);

		verify(messageRepository).findById(messageId);
		verifyNoInteractions(messageMapper);
	}

	@Test
	@DisplayName("메시지 삭제 - 성공")
	public void deleteMessage_success() {
		UUID messageId = UUID.randomUUID();
		given(messageRepository.existsById(messageId)).willReturn(true);
		//
		messageService.delete(messageId);
		//
		verify(messageRepository).existsById(messageId);
		verify(messageRepository).deleteById(messageId);
	}

	@Test
	@DisplayName("메시지 삭제 - 실패")
	public void deleteMessage_fail() {
		UUID messageId = UUID.randomUUID();
		given(messageRepository.existsById(messageId)).willReturn(false);
		//
		assertThatThrownBy(() -> messageService.delete(messageId)).isInstanceOf(MessageNotFoundException.class);
	}

	@Test
	@DisplayName("메시지 findByChannelId - 성공")
	void findByChannelId_success() {
		//
		UUID channelId = UUID.randomUUID();
		Instant createdAt = Instant.parse("2025-01-01T10:00:00Z");
		Pageable pageable = PageRequest.of(0, 10);

		Message mockMessage1 = mock(Message.class);
		Message mockMessage2 = mock(Message.class);
		List<Message> messageList = Arrays.asList(mockMessage1, mockMessage2);

		// 반환값
		Slice<Message> mockSlice = new SliceImpl<>(messageList, pageable, false);

		// 반환값
		MessageDto mockMessageDto1 = new MessageDto(
			UUID.randomUUID(), Instant.parse("2025-01-01T09:00:00Z"),
			null, "mockContent1", null, null, null
		);
		MessageDto mockMessageDto2 = new MessageDto(
			UUID.randomUUID(), Instant.parse("2025-01-01T08:00:00Z"),
			null, "mockContent2", null, null, null
		);

		// 반환값
		PageResponse<MessageDto> expectedResponse = new PageResponse<>(
			Arrays.asList(mockMessageDto1, mockMessageDto2),
			mockMessageDto2.createdAt(),
			2,
			false,
			null
		);

		given(messageRepository.findAllByChannelIdWithAuthor(channelId, createdAt, pageable))
			.willReturn(mockSlice);
		given(messageMapper.toDto(mockMessage1)).willReturn(mockMessageDto1);
		given(messageMapper.toDto(mockMessage2)).willReturn(mockMessageDto2);
		given(pageResponseMapper.fromSlice(any(Slice.class), eq(mockMessageDto2.createdAt())))
			.willReturn(expectedResponse);
		//
		PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, createdAt, pageable);
		//
		assertThat(result).isNotNull();
		assertThat(result.content()).hasSize(2);
		assertThat(result.nextCursor()).isEqualTo(mockMessageDto2.createdAt());
		assertThat(result.size()).isEqualTo(2);
		assertThat(result.hasNext()).isFalse();

		verify(messageRepository).findAllByChannelIdWithAuthor(channelId, createdAt, pageable);
		verify(messageMapper).toDto(mockMessage1);
		verify(messageMapper).toDto(mockMessage2);
		verify(pageResponseMapper).fromSlice(any(Slice.class), eq(mockMessageDto2.createdAt()));
		verifyNoMoreInteractions(messageRepository, messageMapper, pageResponseMapper);
	}

	@Test
	@DisplayName("메시지 findByChannelId - 실패 - 갑자가 DB가 다운될 경우")
	public void findByChannelId_fail() {
		//
		UUID channelId = UUID.randomUUID();
		Instant createdAt = Instant.parse("2025-01-01T10:00:00Z");
		Pageable pageable = PageRequest.of(0, 10);

		given(messageRepository.findAllByChannelIdWithAuthor(channelId, createdAt, pageable))
			.willThrow(new DataAccessResourceFailureException("DB down"));

		//
		assertThatThrownBy(() -> messageService.findAllByChannelId(channelId, createdAt, pageable))
			.isInstanceOf(DataAccessResourceFailureException.class);

		verify(messageRepository).findAllByChannelIdWithAuthor(channelId, createdAt, pageable);
		verifyNoInteractions(messageMapper, pageResponseMapper);
	}

}
