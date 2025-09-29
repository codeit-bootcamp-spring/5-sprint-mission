package com.sprint.mission.controller;

import static com.sprint.mission.discodeit.exception.ErrorCode.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.MessageController;
import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.domain.dto.message.MessageDto;
import com.sprint.mission.discodeit.domain.dto.message.MessageResponse;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.request.MessageCreateRequest;
import com.sprint.mission.discodeit.domain.response.PageResponse;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.MessageService;

@ActiveProfiles("test")
@WebMvcTest({MessageController.class, PageResponseMapper.class})
class MessageControllerTest {

	@Autowired
	MockMvc mockMvc; // http 요청과 응답을 처리해출 mock 객체
	@Autowired
	ObjectMapper objectMapper; // json 처리 도와줄 mapper

	@MockitoBean
	JpaMetamodelMappingContext jpaMetamodelMappingContext;
	@MockitoBean
	MessageService messageService;
	@MockitoBean
	MessageMapper messageMapper;
	@Spy
	private PageResponseMapper pageResponseMapper;

	String username;
	String email;
	String password;

	UUID messageId;
	Instant messageCreatedAt;
	Instant messageUpdatedAt;
	String messageContent;
	UUID channelId;
	List<BinaryContentDto> attachments;

	User user;
	User user2;
	UserDto userDto;
	UserDto userDto2;
	BinaryContent profileBinaryContent;
	BinaryContentDto profileBinaryContentDto;

	Message message;
	BinaryContent attachmentBinaryContent;
	BinaryContentDto attachmentBinaryContentDto;

	byte[] profileBytes;
	long profileSize;
	String profileContentType;
	String profileFilename;
	MultipartFile profileRequest;

	byte[] attachmentBytes;
	long attachmentSize;
	String attachmentContentType;
	String attachmentFilename;
	MultipartFile attachmentRequest;

	@BeforeEach
	void setUp() {
		profileBytes = "profileBytes".getBytes();
		profileSize = "profileSize".getBytes().length;
		profileContentType = "profileContentType";
		profileFilename = "profileFilename";

		attachmentBytes = "attachmentBytes".getBytes();
		attachmentSize = "attachmentSize".getBytes().length;
		attachmentContentType = "attachmentContentType";
		attachmentFilename = "attachmentFilename";

		username = "username";
		email = "email";
		password = "password";

		profileRequest = new MockMultipartFile(
		  "file",               // form field name (컨트롤러 @RequestParam("file")와 매칭)
		  profileFilename,             // original filename
		  profileContentType,          // content type
		  profileBytes                 // file content
		);

		profileBinaryContent = BinaryContent.builder()
		  .id(UUID.randomUUID())
		  .fileName(profileFilename)
		  .contentType(profileContentType)
		  .size(profileSize)
		  .build();
		profileBinaryContentDto = BinaryContentDto.builder()
		  .id(profileBinaryContent.getId())
		  .fileName(profileFilename)
		  .size(profileSize)
		  .contentType(profileContentType)
		  .build();

		attachmentBinaryContent = BinaryContent.builder()
		  .id(UUID.randomUUID())
		  .fileName(attachmentFilename)
		  .contentType(attachmentContentType)
		  .size(attachmentSize)
		  .build();
		attachmentBinaryContentDto = BinaryContentDto.builder()
		  .id(attachmentBinaryContent.getId())
		  .fileName(attachmentFilename)
		  .size(attachmentSize)
		  .contentType(attachmentContentType)
		  .build();

		user = User.builder()
		  .id(UUID.randomUUID())
		  .username(username)
		  .email(email)
		  .password(password)
		  .profileImage(profileBinaryContent)
		  .build();

		userDto = UserDto.builder()
		  .id(user.getId())
		  .username(username)
		  .email(email)
		  .profile(profileBinaryContentDto)
		  .online(true)
		  .build();

		messageId = UUID.randomUUID();
		messageCreatedAt = Instant.now();
		messageUpdatedAt = null;
		messageContent = "messageContent";
		channelId = UUID.randomUUID();
		attachments = List.of(attachmentBinaryContentDto);
	}

	@Test
	@DisplayName("메시지 생성 테스트 올바른 입력값이 주어졌을 때")
	void createMessageWithRightInputTest() throws Exception {
		MessageDto messageDto = MessageDto.builder()
		  .id(messageId)
		  .createdAt(messageCreatedAt)
		  .updatedAt(messageUpdatedAt)
		  .content(messageContent)
		  .channelId(channelId)
		  .author(userDto)
		  .attachments(attachments)
		  .build();

		MessageResponse messageResponse = MessageResponse.builder()
		  .id(messageId)
		  .createdAt(messageCreatedAt)
		  .updatedAt(messageUpdatedAt)
		  .content(messageContent)
		  .channelId(channelId)
		  .author(userDto)
		  .attachments(attachments)
		  .build();

		given(messageService.create(any())).willReturn(messageDto);
		given(messageMapper.toResponse(any())).willReturn(messageResponse);

		MessageCreateRequest request = MessageCreateRequest.builder()
		  .content(messageContent)
		  .authorId(user.getId())
		  .channelId(channelId)
		  .build();

		MockMultipartFile jsonPart = new MockMultipartFile(
		  "messageCreateRequest",       // 컨트롤러의 @RequestPart 이름
		  "messageCreateRequest.json",
		  "application/json",
		  objectMapper.writeValueAsBytes(request)
		);

		// 파일 part
		MockMultipartFile profilePart = new MockMultipartFile(
		  attachmentFilename,                 // 컨트롤러의 @RequestPart 이름
		  attachmentFilename + ".png",
		  attachmentContentType,
		  attachmentBytes
		);
		mockMvc.perform(multipart("/api/messages")
			.file(jsonPart)
			.file(profilePart)
			.content(objectMapper.writeValueAsString(request)))
		  .andExpect(status().isCreated())
		  .andExpect(jsonPath("$.id").value(messageId.toString()))
		  .andExpect(jsonPath("$.createdAt").value(messageCreatedAt.toString()))
		  .andExpect(jsonPath("$.updatedAt").value(nullValue()))
		  .andExpect(jsonPath("$.content").value(messageContent))
		  .andExpect(jsonPath("$.channelId").value(channelId.toString()))

		  .andExpect(jsonPath("$.author.id").value(user.getId().toString()))
		  .andExpect(jsonPath("$.author.username").value(username))
		  .andExpect(jsonPath("$.author.email").value(email))
		  .andExpect(jsonPath("$.author.profile.id").value(profileBinaryContent.getId().toString()))
		  .andExpect(jsonPath("$.author.profile.fileName").value(profileFilename))
		  .andExpect(jsonPath("$.author.profile.size").value(profileSize))
		  .andExpect(jsonPath("$.author.profile.contentType").value(profileContentType))
		  .andExpect(jsonPath("$.author.online").value(userDto.getOnline()))

		  .andExpect(jsonPath("$.attachments[0].id").value(attachmentBinaryContent.getId().toString()))
		  .andExpect(jsonPath("$.attachments[0].fileName").value(attachmentFilename))
		  .andExpect(jsonPath("$.attachments[0].size").value(attachmentSize))
		  .andExpect(jsonPath("$.attachments[0].contentType").value(attachmentContentType))
		;

	}

	@Test
	@DisplayName("메시지 생성 테스트 -잘못된 입력 : 빈 내용이 주어졌을 때")
	void createMessageWithWrongInputBlankContent() throws Exception {

		MessageCreateRequest request = MessageCreateRequest.builder()
		  .content("")
		  .authorId(user.getId())
		  .channelId(channelId)
		  .build();

		MockMultipartFile jsonPart = new MockMultipartFile(
		  "messageCreateRequest",       // 컨트롤러의 @RequestPart 이름
		  "messageCreateRequest.json",
		  "application/json",
		  objectMapper.writeValueAsBytes(request)
		);

		mockMvc.perform(multipart("/api/messages")
			.file(jsonPart))
		  .andExpect(status().isBadRequest())
		  .andExpect(jsonPath("$.code").value(VALIDATION_ERROR.name()))
		  .andExpect(jsonPath("$.message").value(VALIDATION_ERROR.getMessage()));

	}

	@Test
	@DisplayName("메시지 목록 조회 테스트 Cursor가 주어졌을 때")
	void getMessagesInChannelWithCursor() throws Exception {

		MessageDto messageDto1 = MessageDto.builder()
		  .id(messageId)
		  .createdAt(messageCreatedAt)
		  .updatedAt(messageUpdatedAt)
		  .content(messageContent)
		  .channelId(channelId)
		  .author(userDto)
		  .attachments(attachments)
		  .build();
		MessageDto messageDto2 = MessageDto.builder()
		  .id(UUID.randomUUID())
		  .createdAt(Instant.now())
		  .updatedAt(null)
		  .content(messageContent)
		  .channelId(channelId)
		  .author(userDto)
		  .attachments(null)
		  .build();

		MessageResponse messageResponse1 = MessageResponse.builder()
		  .id(messageId)
		  .createdAt(messageCreatedAt)
		  .updatedAt(messageUpdatedAt)
		  .content(messageContent)
		  .channelId(channelId)
		  .author(userDto)
		  .attachments(attachments)
		  .build();

		MessageResponse messageResponse2 = MessageResponse.builder()
		  .id(messageDto2.getId())
		  .createdAt(messageDto2.getCreatedAt())
		  .updatedAt(messageDto2.getUpdatedAt())
		  .content(messageDto2.getContent())
		  .channelId(messageDto2.getChannelId())
		  .author(messageDto2.getAuthor())
		  .attachments(messageDto2.getAttachments())
		  .build();

		PageResponse<MessageResponse> pageResponse = PageResponse.<MessageResponse>builder()
		  .content(List.of(messageResponse2, messageResponse1))
		  .nextCursor(messageResponse1)
		  .size(10)
		  .hasNext(false)
		  .totalElements(2L)
		  .build();

		Slice<MessageDto> messageResult = new SliceImpl<>(
		  List.of(messageDto2, messageDto1),
		  PageRequest.of(0, 10),
		  false);
		given(messageService.findAllCursorByChannelId(any(), any(), any())).willReturn(messageResult);
		given(messageMapper.toResponse(messageDto1)).willReturn(messageResponse1);
		given(messageMapper.toResponse(messageDto2)).willReturn(messageResponse2);

		mockMvc.perform(get("/api/messages")
			.param("channelId", channelId.toString())
			.param("cursor", Instant.now().toString())
			.param("page", "0")
			.param("size", "10")
			.param("sort", "createdAt,desc"))
		  .andExpect(status().isOk())
		  .andExpect(content().json(objectMapper.writeValueAsString(pageResponse)));

	}

	@Test
	@DisplayName("메시지 목록 조회 테스트 Cursor가 안주어졌을 때")
	void getMessagesInChannelWithoutCursor() throws Exception {

		MessageDto messageDto1 = MessageDto.builder()
		  .id(messageId)
		  .createdAt(messageCreatedAt)
		  .updatedAt(messageUpdatedAt)
		  .content(messageContent)
		  .channelId(channelId)
		  .author(userDto)
		  .attachments(attachments)
		  .build();
		MessageDto messageDto2 = MessageDto.builder()
		  .id(UUID.randomUUID())
		  .createdAt(Instant.now())
		  .updatedAt(null)
		  .content(messageContent)
		  .channelId(channelId)
		  .author(userDto)
		  .attachments(null)
		  .build();

		MessageResponse messageResponse1 = MessageResponse.builder()
		  .id(messageId)
		  .createdAt(messageCreatedAt)
		  .updatedAt(messageUpdatedAt)
		  .content(messageContent)
		  .channelId(channelId)
		  .author(userDto)
		  .attachments(attachments)
		  .build();

		MessageResponse messageResponse2 = MessageResponse.builder()
		  .id(messageDto2.getId())
		  .createdAt(messageDto2.getCreatedAt())
		  .updatedAt(messageDto2.getUpdatedAt())
		  .content(messageDto2.getContent())
		  .channelId(messageDto2.getChannelId())
		  .author(messageDto2.getAuthor())
		  .attachments(messageDto2.getAttachments())
		  .build();

		PageResponse<MessageResponse> pageResponse = PageResponse.<MessageResponse>builder()
		  .content(List.of(messageResponse2, messageResponse1))
		  .nextCursor(messageResponse1)
		  .size(10)
		  .hasNext(false)
		  .totalElements(2L)
		  .build();

		Page<MessageDto> messageResult = new PageImpl(
		  List.of(messageDto2, messageDto1),
		  PageRequest.of(0, 10),
		  2
		);
		given(messageService.findAllByChannelId(any(), any())).willReturn(messageResult);
		given(messageMapper.toResponse(messageDto1)).willReturn(messageResponse1);
		given(messageMapper.toResponse(messageDto2)).willReturn(messageResponse2);

		mockMvc.perform(get("/api/messages")
			.param("channelId", channelId.toString())
			.param("page", "0")
			.param("size", "10")
			.param("sort", "createdAt,desc"))
		  .andExpect(status().isOk())
		  .andExpect(content().json(objectMapper.writeValueAsString(pageResponse)));
	}

	@Test
	void updateMessage() {
	}

	@Test
	void deleteMessage() {
	}

}