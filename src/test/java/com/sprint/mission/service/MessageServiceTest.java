package com.sprint.mission.service;

import static com.sprint.mission.discodeit.domain.enums.ChannelType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.anyBoolean;
import static org.mockito.BDDMockito.*;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import com.sprint.mission.discodeit.domain.dto.CreateBiContentDTO;
import com.sprint.mission.discodeit.domain.dto.CreateMessageDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateMessageDTO;
import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.domain.dto.message.MessageDto;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.enums.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.AuthorNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;

@ExtendWith(MockitoExtension.class)
@EnableJpaAuditing
@ActiveProfiles("test")
public class MessageServiceTest {
	@InjectMocks
	private BasicMessageService messageService;

	@Mock
	private MessageRepository messageRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ChannelRepository channelRepository;
	@Mock
	private BinaryContentRepository binaryContentRepository;
	@Mock
	private BasicBinaryContentService binaryContentService;
	@Mock
	private MessageMapper messageMapper;
	@Mock
	private UserMapper userMapper;
	@Mock
	private UserStatusRepository userStatusRepository;
	@Mock
	private BinaryContentMapper binaryContentMapper;

	String username;
	String email;
	String password;

	String fileName;
	String contentType;
	long size;
	byte[] attachmentContent;

	String messageContent;
	String messageContent2;
	String messageContent3;

	Message mockMessage;
	Message mockMessage2;
	Message mockMessage3;

	MessageDto mockMessageDto;
	MessageDto mockMessageDto2;
	MessageDto mockMessageDto3;

	User mockUser;
	UserDto mockUserDto;
	UserStatus mockUserStatus;

	Channel mockChannel;

	BinaryContent mockAttachment;
	BinaryContentDto mockAttachmentDto;
	CreateBiContentDTO mockCreateAttachmentDto;

	@BeforeEach
	public void setUp() {
		ChannelType channelType = PUBLIC;
		String channelName = "channelName";
		String channelDescription = "channelDescription";

		username = "username";
		email = "email";
		password = "password";

		fileName = "fileName";
		contentType = "contentType";
		size = 123L;
		attachmentContent = "contents".getBytes();

		messageContent = "messageContent";
		messageContent2 = "messageContent2";
		messageContent3 = "messageContent3";

		mockAttachment = BinaryContent.builder()
		  .id(UUID.randomUUID())
		  .fileName(fileName)
		  .contentType(contentType)
		  .size(size)
		  .build();
		mockCreateAttachmentDto = CreateBiContentDTO.builder()
		  .content(attachmentContent)
		  .size(size)
		  .fileName(fileName)
		  .build();
		mockAttachmentDto = BinaryContentDto.builder()
		  .id(mockAttachment.getId())
		  .fileName(fileName)
		  .size(size)
		  .contentType(contentType)
		  .build();
		mockUser = User.builder()
		  .id(UUID.randomUUID())
		  .username(username)
		  .email(email)
		  .password(password)
		  .profileImage(null)
		  .build();
		mockUserStatus = new UserStatus(mockUser);
		mockUserDto = UserDto.builder()
		  .id(mockUser.getId())
		  .username(mockUser.getUsername())
		  .email(mockUser.getEmail())
		  .profile(null)
		  .online(mockUserStatus.isOnline())
		  .build();

		mockChannel = Channel.builder()
		  .type(channelType)
		  .name(channelName)
		  .description(channelDescription)
		  .build();

		mockMessage = new Message(messageContent, mockUser, mockChannel, List.of(mockAttachment));
		mockMessage2 = new Message(messageContent2, mockUser, mockChannel, List.of());
		mockMessage3 = new Message(messageContent3, mockUser, mockChannel, List.of());

		mockMessageDto2 = MessageDto.builder()
		  .id(mockMessage2.getId())
		  .createdAt(mockMessage2.getCreatedAt())
		  .updatedAt(mockMessage2.getUpdatedAt())
		  .content(mockMessage2.getContent())
		  .channelId(mockMessage2.getChannel().getId())
		  .author(mockUserDto)
		  .attachments(List.of(mockAttachmentDto))
		  .build();
		mockMessageDto3 = MessageDto.builder()
		  .id(mockMessage3.getId())
		  .createdAt(mockMessage3.getCreatedAt())
		  .updatedAt(mockMessage3.getUpdatedAt())
		  .content(mockMessage3.getContent())
		  .channelId(mockMessage3.getChannel().getId())
		  .author(mockUserDto)
		  .attachments(List.of(mockAttachmentDto))
		  .build();
		mockMessageDto = MessageDto.builder()
		  .id(mockMessage.getId())
		  .createdAt(mockMessage.getCreatedAt())
		  .updatedAt(mockMessage.getUpdatedAt())
		  .content(mockMessage.getContent())
		  .channelId(mockMessage.getChannel().getId())
		  .author(mockUserDto)
		  .attachments(List.of(mockAttachmentDto))
		  .build();

		mockMessage2 = new Message(messageContent2, mockUser, mockChannel, List.of());
		mockMessage3 = new Message(messageContent3, mockUser, mockChannel, List.of());

	}

	@Test
	@DisplayName("메시지 생성 테스트 첨부파일이 주어졌을 때 ")
	public void messageCreateTestWithRightInput() {
		// Given
		given(channelRepository.findById(any())).willReturn(Optional.of(mockChannel));
		given(userRepository.findUserWithProfileImageByID(any())).willReturn(Optional.of(mockUser));
		given(userStatusRepository.findByUserId(any())).willReturn(Optional.of(mockUserStatus));
		given(binaryContentService.create(any())).willReturn(mockAttachment);
		given(messageRepository.save(any())).willReturn(mockMessage);
		given(userMapper.toDto(any(), anyBoolean(), any())).willReturn(mockUserDto);
		given(messageMapper.toDto(any(), any())).willReturn(mockMessageDto);

		CreateMessageDTO command = CreateMessageDTO.builder()
		  .content(messageContent)
		  .channelId(mockChannel.getId())
		  .userId(mockUser.getId())
		  .attachments(List.of(mockCreateAttachmentDto))
		  .build();
		// When
		MessageDto result = messageService.create(command);

		assertThat(result).isEqualTo(mockMessageDto);

	}

	@Test
	@DisplayName("메시지 생성 테스트 잘못된 입력 - 채널이 존재하지 않을 때 ")
	public void messageCreateTestWithNotFoundChannel() {
		// Given
		given(channelRepository.findById(any())).willReturn(Optional.empty());

		CreateMessageDTO command = CreateMessageDTO.builder()
		  .content(messageContent)
		  .channelId(mockChannel.getId())
		  .userId(mockUser.getId())
		  .attachments(List.of(mockCreateAttachmentDto))
		  .build();
		assertThatThrownBy(() -> messageService.create(command))
		  .isInstanceOf(ChannelNotFoundException.class)
		  .hasMessageContaining("채널을 찾을 수 없습니다.");

	}

	@Test
	@DisplayName("메시지 생성 테스트 잘못된 입력 - 글쓴이가 존재하지 않을 때 ")
	public void messageCreateTestWithNotFoundAuthor() {
		// Given
		given(channelRepository.findById(any())).willReturn(Optional.of(mockChannel));
		given(userRepository.findUserWithProfileImageByID(any())).willReturn(Optional.empty());

		CreateMessageDTO command = CreateMessageDTO.builder()
		  .content(messageContent)
		  .channelId(mockChannel.getId())
		  .userId(mockUser.getId())
		  .attachments(List.of(mockCreateAttachmentDto))
		  .build();
		assertThatThrownBy(() -> messageService.create(command))
		  .isInstanceOf(AuthorNotFoundException.class)
		  .hasMessageContaining("글쓴이를 찾을 수 없습니다.");

	}

	@Test
	@DisplayName("메시지 삭제 테스트 올바른 입력")
	public void messageDeleteTestWithRightInput() {
		// Given
		given(messageRepository.findById(any())).willReturn(Optional.of(mockMessage));
		doNothing().when(binaryContentRepository).deleteByIdIn(any());
		doNothing().when(messageRepository).deleteById(any());

		// When
		messageService.delete(mockMessage.getId());

	}

	@Test
	@DisplayName("메시지 삭제 테스트 잘못된 입력 - 메시지를 찾을 수 없음")
	public void messageDeleteTestWithMessageNotFound() {
		given(messageRepository.findById(any())).willReturn(Optional.empty());

		assertThatThrownBy(() -> messageService.delete(mockMessage.getId()))
		  .isInstanceOf(MessageNotFoundException.class)
		  .hasMessageContaining("메시지를 찾을 수 없습니다.");

	}

	@Test
	@DisplayName("메시지 갱신 테스트 올바른 입력")
	public void messageUpdateTestWithRightInput() {
		// Given
		String newContent = "new content";
		MessageDto newMessageDto = MessageDto.builder()
		  .id(mockMessage.getId())
		  .createdAt(mockMessage.getCreatedAt())
		  .updatedAt(Instant.now())
		  .content(newContent)
		  .channelId(mockMessage.getChannel().getId())
		  .author(mockUserDto)
		  .attachments(List.of(mockAttachmentDto))
		  .build();

		given(messageRepository.findMessageDetailsById(any())).willReturn(Optional.of(mockMessage));
		given(userStatusRepository.findByUserId(any())).willReturn(Optional.of(mockUserStatus));
		given(messageRepository.save(any())).willReturn(null);
		given(userMapper.toDto(any(), anyBoolean(), any())).willReturn(mockUserDto);
		given(messageMapper.toDto(any(), any())).willReturn(newMessageDto);
		given(binaryContentMapper.toDto(any())).willReturn(mockAttachmentDto);

		UpdateMessageDTO command = UpdateMessageDTO.builder()
		  .id(mockMessage.getId())
		  .newContent(newContent)
		  .build();
		// When
		MessageDto result = messageService.update(command);

		assertThat(result).isEqualTo(newMessageDto);
	}

	@Test
	@DisplayName("메시지 갱신 테스트 잘못된 입력 - 메시지를 찾을 수 없음")
	public void messageUpdateTestWithMessageNotFound() {
		given(messageRepository.findMessageDetailsById(any())).willReturn(Optional.empty());

		UpdateMessageDTO command = UpdateMessageDTO.builder()
		  .id(mockMessage.getId())
		  .newContent("newContent")
		  .build();

		assertThatThrownBy(() -> messageService.update(command))
		  .isInstanceOf(MessageNotFoundException.class)
		  .hasMessageContaining("메시지를 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("메시지 목록 조회 올바른 입력값")
	public void messageFindTestWithRightValue() {

		Page<Message> mockMessageEntiesPage = new PageImpl<>(
		  List.of(mockMessage, mockMessage2, mockMessage3)
		  , PageRequest.of(0, 10)
		  , 3);

		mockMessageDto = MessageDto.builder()
		  .id(mockMessage.getId())
		  .createdAt(mockMessage.getCreatedAt())
		  .updatedAt(mockMessage.getUpdatedAt())
		  .content(mockMessage.getContent())
		  .channelId(mockMessage.getChannel().getId())
		  .author(mockUserDto)
		  .attachments(List.of(mockAttachmentDto))
		  .build();

		given(messageRepository.findAllDetailsByChannelId(mockChannel.getId(), PageRequest.of(0, 10)))
		  .willReturn(mockMessageEntiesPage);
		given(userStatusRepository.findByUserIdIn(any())).willReturn(List.of(mockUserStatus));
		given(binaryContentMapper.toDto(null)).willReturn(null);
		given(userMapper.toDto(any(), anyBoolean(), any())).willReturn(mockUserDto);
		given(messageMapper.toDto(mockMessage, mockUserDto)).willReturn(mockMessageDto);
		given(messageMapper.toDto(mockMessage2, mockUserDto)).willReturn(mockMessageDto2);
		given(messageMapper.toDto(mockMessage3, mockUserDto)).willReturn(mockMessageDto3);

		// When
		Page<MessageDto> result = messageService.findAllByChannelId(mockChannel.getId(), PageRequest.of(0, 10));

		// Then
		assertAll(
		  () -> assertThat(result.getTotalElements()).isEqualTo(3),
		  () -> assertThat(result.getNumber()).isEqualTo(0),
		  () -> assertThat(result.getContent()
			.containsAll(List.of(mockMessageDto, mockMessageDto2, mockMessageDto3))).isTrue()
		);

	}

	@Test
	@DisplayName("메시지 목록 조회 - 커서기반  올바른 입력값")
	public void messageFindByCursorTestWithRightValue() {

		// Given
		Page<Message> mockMessageEntiesPage = new PageImpl<>(
		  List.of(mockMessage, mockMessage2, mockMessage3)
		  , PageRequest.of(0, 10)
		  , 3);
		given(messageRepository.findAllDetailsByChannelIdAndCursor(any(), any(), any()))
		  .willReturn(mockMessageEntiesPage);
		given(userStatusRepository.findByUserIdIn(any())).willReturn(List.of(mockUserStatus));
		given(binaryContentMapper.toDto(null)).willReturn(null);
		given(userMapper.toDto(any(), anyBoolean(), any())).willReturn(mockUserDto);
		given(messageMapper.toDto(mockMessage, mockUserDto)).willReturn(mockMessageDto);
		given(messageMapper.toDto(mockMessage2, mockUserDto)).willReturn(mockMessageDto2);
		given(messageMapper.toDto(mockMessage3, mockUserDto)).willReturn(mockMessageDto3);
		// When
		Slice<MessageDto> result = messageService.findAllCursorByChannelId
		  (mockChannel.getId(), Instant.MIN, PageRequest.of(0, 10));

		// Then
		assertAll(
		  () -> assertThat(result.getContent().size()).isEqualTo(3),
		  () -> assertThat(result.getNumber()).isEqualTo(0),
		  () -> assertThat(result.getContent()
			.containsAll(List.of(mockMessageDto, mockMessageDto2, mockMessageDto3))).isTrue()
		);
	}
}
