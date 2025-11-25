package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import com.sprint.mission.discodeit.dto.PageResponse;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.NewBinaryContent;
import com.sprint.mission.discodeit.dto.message.MessageCreateCommand;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Role;
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
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

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
	private MessageMapper messageMapper;
	@Mock
	private PageResponseMapper pageResponseMapper;

	@InjectMocks
	private BasicMessageService messageService;

	private MessageCreateCommand command;
	private Message message;
	private MessageDto messageDto;
	private User author;
	private Channel channel;
	private UserDto userDto;
	private BinaryContent binaryContent;
	private BinaryContentDto binaryContentDto;
	private NewBinaryContent newBinaryContent;
	private Instant cursor;
	private Pageable pageable;

	@BeforeEach
	public void setUp() {
		channel = new Channel(
			ChannelType.PUBLIC,
			"test name",
			"test description"
		);
		ReflectionTestUtils.setField(channel, "id", UUID.randomUUID());
		author = new User(
			"test",
			"test@email.com",
			"12341234",
			null
		);
		ReflectionTestUtils.setField(author, "id", UUID.randomUUID());
		userDto = new UserDto(
			author.getId(),
			author.getUsername(),
			author.getEmail(),
			null,
			false,
			Role.USER
		);
		command = new MessageCreateCommand(
			channel.getId(),
			author.getId(),
			"test content",
			List.of()
		);
		message = new Message(
			"test content",
			channel,
			author,
			null
		);
		binaryContent = new BinaryContent(
			"profile",
			"image/jpg",
			"jpg".getBytes().length);
		ReflectionTestUtils.setField(binaryContent, "id", UUID.randomUUID());

		binaryContentDto = new BinaryContentDto(
			binaryContent.getId(),
			"profile",
			(long)"jpg".getBytes().length,
			"image/jpg"
		);

		newBinaryContent = new NewBinaryContent(
			"profile",
			"image/jpg",
			"jpg".getBytes(StandardCharsets.UTF_8)
		);

		ReflectionTestUtils.setField(message, "id", UUID.randomUUID());
		ReflectionTestUtils.setField(message, "createdAt", Instant.now());
		ReflectionTestUtils.setField(message, "updatedAt", Instant.now());

		messageDto = MessageDto.builder()
			.id(UUID.randomUUID())
			.createdAt(message.getCreatedAt())
			.updatedAt(message.getUpdatedAt())
			.content(message.getContent())
			.channelId(channel.getId())
			.author(userDto)
			.attachments(List.of())
			.build();

		pageable = PageRequest.of(0, 2);
	}

	@Test
	@DisplayName("메세지 생성 테스트(첨부 파일 없음)")
	void createMessageWithoutAttachments() {
		given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);
		given(messageRepository.save(any(Message.class))).willReturn(message);
		given(channelRepository.findById(any())).willReturn(Optional.of(channel));
		given(userRepository.findById(any())).willReturn(Optional.of(author));

		MessageDto result = messageService.create(command);

		assertThat(result).isEqualTo(messageDto);

		verify(messageRepository, times(1)).save(any(Message.class));
		verify(messageRepository, times(1)).save(any(Message.class));
		verify(channelRepository, times(1)).findById(any());
		verify(userRepository, times(1)).findById(any());
	}

	@Test
	@DisplayName("메세지 생성 테스트(첨부 파일 있음)")
	void createMessageWithAttachments() {
		message = new Message(
			"test content",
			channel,
			author,
			List.of(binaryContent)
		);
		messageDto = MessageDto.builder()
			.id(UUID.randomUUID())
			.createdAt(message.getCreatedAt())
			.updatedAt(message.getUpdatedAt())
			.content(message.getContent())
			.channelId(channel.getId())
			.author(userDto)
			.attachments(List.of(binaryContentDto))
			.build();
		command = new MessageCreateCommand(
			channel.getId(),
			author.getId(),
			"test content",
			List.of(newBinaryContent)
		);

		given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);
		given(messageRepository.save(any(Message.class))).willReturn(message);
		given(channelRepository.findById(any())).willReturn(Optional.of(channel));
		given(userRepository.findById(any())).willReturn(Optional.of(author));
		given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);
		given(binaryContentStorage.put(any(), any())).willReturn(binaryContent.getId());

		MessageDto result = messageService.create(command);

		assertThat(result).isEqualTo(messageDto);

		verify(messageRepository, times(1)).save(any(Message.class));
		verify(messageRepository, times(1)).save(any(Message.class));
		verify(channelRepository, times(1)).findById(any());
		verify(userRepository, times(1)).findById(any());
		verify(binaryContentRepository, times(1)).save(any(BinaryContent.class));
		verify(binaryContentStorage, times(1)).put(any(), any());
	}

	@Test
	@DisplayName("메세지 생성 실패(사용자를 찾을 수 없음)")
	void createMessageFailureUserNotFound() {
		given(userRepository.findById(any())).willReturn(Optional.empty());
		assertThatThrownBy(() -> messageService.create(command))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@DisplayName("메세지 생성 실패(채널을 찾을 수 없음)")
	void createMessageFailureChannelNotFound() {
		given(userRepository.findById(any())).willReturn(Optional.of(author));
		given(channelRepository.findById(any())).willReturn(Optional.empty());
		assertThatThrownBy(() -> messageService.create(command))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@DisplayName("메세지 업데이트 테스트")
	void updateMessage() {
		MessageUpdateRequest request = new MessageUpdateRequest("update content");

		messageDto = MessageDto.builder()
			.id(message.getId())
			.createdAt(message.getCreatedAt())
			.updatedAt(message.getUpdatedAt())
			.content("update content")
			.channelId(channel.getId())
			.author(userDto)
			.attachments(List.of())
			.build();

		given(messageRepository.findById(any())).willReturn(Optional.of(message));
		given(messageRepository.save(any(Message.class))).willReturn(message);
		given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

		MessageDto result = messageService.update(message.getId(), request);

		assertThat(result).isEqualTo(messageDto);
		verify(messageRepository, times(1)).save(any(Message.class));
		verify(messageRepository, times(1)).findById(any());
		verify(messageMapper, times(1)).toDto(any(Message.class));
	}

	@Test
	@DisplayName("메세지 업데이트 실패(메세지를 찾을 수 없음)")
	void messageUpdateFailureMessageNotFound() {
		given(messageRepository.findById(any())).willReturn(Optional.empty());
		assertThatThrownBy(() -> messageService.update(UUID.randomUUID(), new MessageUpdateRequest("")))
			.isInstanceOf(MessageNotFoundException.class);
	}

	@Test
	@DisplayName("메세지 삭제 테스트")
	void messageDeleteWithoutAttachments() {
		given(messageRepository.findById(any())).willReturn(Optional.of(message));
		messageService.delete(message.getId());

		verify(messageRepository, times(1)).findById(any());
		verify(messageRepository, times(1)).deleteById(any());
	}

	@Test
	@DisplayName("메세지 삭제 테스트")
	void messageDeleteWithAttachments() {
		message = new Message(
			"test message",
			channel,
			author,
			List.of(binaryContent)
		);
		given(messageRepository.findById(any())).willReturn(Optional.of(message));
		messageService.delete(message.getId());

		verify(messageRepository, times(1)).findById(any());
		verify(messageRepository, times(1)).deleteById(any());
		verify(binaryContentRepository, times(1)).deleteById(any());
	}

	@Test
	@DisplayName("메세지 삭제 실패")
	void messageDeleteFailureMessageNotFound() {
		given(messageRepository.findById(any())).willReturn(Optional.empty());
		assertThatThrownBy(() -> messageService.delete(UUID.randomUUID()))
			.isInstanceOf(MessageNotFoundException.class);
	}

	@Test
	@DisplayName("채널 id로 메세지 조회 테스트(첫 페이지)")
	void findAllByChannelIdFirstPage() {
		given(channelRepository.existsById(any())).willReturn(true);

		cursor = null;
		Slice<Message> messages = new SliceImpl<>(List.of(message), pageable, true);
		given(messageRepository.findAllByChannelIdOrderByCreatedAtDescIdDesc(any(), any()))
			.willReturn(messages);
		given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

		ArgumentCaptor<Instant> nextCursorCap = ArgumentCaptor.forClass(Instant.class);

		@SuppressWarnings("unchecked")
		PageResponse<MessageDto> dummy = mock(PageResponse.class);

		given(pageResponseMapper.fromSlice(
			ArgumentMatchers.<Slice<MessageDto>>any(), // ← 타입 지정!
			nextCursorCap.capture()
		)).willReturn(dummy);

		PageResponse<MessageDto> result = messageService.findAllByChannelId(channel.getId(), cursor,
			pageable);

		assertThat(result).isEqualTo(dummy);
	}

	@Test
	@DisplayName("메세지 조회 실패(채널이 존재하지 않음)")
	void findAllByChannelIdFailureChannelNotFound() {
		given(channelRepository.existsById(any())).willReturn(false);
		assertThatThrownBy(() -> messageService.findAllByChannelId(UUID.randomUUID(), cursor, pageable))
			.isInstanceOf(ChannelNotFoundException.class);
	}

}
