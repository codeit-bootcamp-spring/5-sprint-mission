package com.sprint.mission.integration;

import static com.sprint.mission.discodeit.domain.enums.ChannelType.*;
import static com.sprint.mission.discodeit.exception.ErrorCode.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.domain.dto.CreateUserDTO;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.request.MessageCreateRequest;
import com.sprint.mission.discodeit.domain.request.UpdateMessageRequest;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import jakarta.persistence.EntityManager;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MessageApiTest {

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	MockMvc mockMvc;
	@Autowired
	UserRepository userRepository;
	@Autowired
	UserStatusRepository userStatusRepository;

	@Autowired
	EntityManager em;

	@Autowired
	private BasicUserService basicUserService;
	@Autowired
	private BinaryContentRepository binaryContentRepository;
	@Autowired
	private BasicBinaryContentService basicBinaryContentService;
	@MockitoBean
	private S3Client s3Client;
	@MockitoBean
	private S3Presigner s3Presigner;

	String username;
	String email;

	byte[] bytes;
	long size;
	String contentType;
	String filename;

	BinaryContent attachment1;
	BinaryContent attachment2;

	String messageContent;
	UserDto authorDto;
	Channel channel;
	List<BinaryContent> attachments;
	Message storedMessage;
	Message storedMessage2;
	@Autowired
	private ChannelRepository channelRepository;
	@Autowired
	private BasicMessageService basicMessageService;
	@Autowired
	private MessageRepository messageRepository;

	@BeforeEach
	void setUp() {
		username = "username";
		email = "email@email.com";

		bytes = "bytes".getBytes();
		size = bytes.length;
		contentType = MediaType.IMAGE_JPEG_VALUE;
		filename = "filename.jpg";

		attachment1 = BinaryContent.builder()
		  .fileName(filename)
		  .contentType(contentType)
		  .size(filename.getBytes().length)
		  .build();
		attachment2 = BinaryContent.builder()
		  .fileName(filename + "2")
		  .contentType(contentType + "2")
		  .size((filename + "2").getBytes().length)
		  .build();

		messageContent = "content";
		authorDto = basicUserService.create(CreateUserDTO.builder()
		  .username(username)
		  .email(email)
		  .password("password")
		  .binaryContent(null)
		  .build());
		User author = userRepository.findById(authorDto.getId()).get();

		channel = channelRepository.save(Channel.builder()
		  .name("channelName")
		  .type(PUBLIC)
		  .description("description")
		  .build());
		attachments = List.of(attachment1, attachment2);

		storedMessage = messageRepository.save(new Message(messageContent, author, channel, null));
		storedMessage2 = messageRepository.save(new Message(messageContent, author, channel, null));

	}

	@Test
	@DisplayName("메시지 생성 테스트")
	void createMessage() throws Exception {
		MessageCreateRequest request = MessageCreateRequest.builder()
		  .content(messageContent)
		  .authorId(authorDto.getId())
		  .channelId(channel.getId())
		  .build();

		MockMultipartFile jsonPart = new MockMultipartFile(
		  "messageCreateRequest",
		  "messageCreateRequest.json",
		  MediaType.APPLICATION_JSON_VALUE,
		  objectMapper.writeValueAsBytes(request)
		);

		MockMultipartFile avatarPart1 = new MockMultipartFile(
		  "attachments",
		  attachment1.getFileName(),
		  attachment1.getContentType(),
		  attachment1.getFileName().getBytes()
		);

		MockMultipartFile avatarPart2 = new MockMultipartFile(
		  "attachments",
		  attachment2.getFileName(),
		  attachment2.getContentType(),
		  attachment2.getFileName().getBytes()
		);

		mockMvc.perform(multipart("/api/messages")
			.file(jsonPart)
			.file(avatarPart1)
			.file(avatarPart2)
		  )
		  .andExpect(status().isCreated())
		  .andExpect(jsonPath("$.id", notNullValue()))
		  .andExpect(jsonPath("$.createdAt", notNullValue()))
		  .andExpect(jsonPath("$.content").value(messageContent))
		  .andExpect(jsonPath("$.content").value(messageContent))
		  .andExpect(jsonPath("$.channelId").value(channel.getId().toString()))
		  .andExpect(jsonPath("$.author.id").value(authorDto.getId().toString()))
		  .andExpect(jsonPath("$.author.username").value(username))
		  .andExpect(jsonPath("$.author.email").value(email))
		  .andExpect(jsonPath("$.author.profile", nullValue()))
		  .andExpect(jsonPath("$.author.online").value(true))
		  .andExpect(jsonPath("$.attachments[0].id", notNullValue()))
		  .andExpect(jsonPath("$.attachments[0].fileName").value(filename))
		  .andExpect(jsonPath("$.attachments[0].size").value(attachment1.getSize()))
		  .andExpect(jsonPath("$.attachments[0].contentType").value(attachment1.getContentType()))
		  .andExpect(jsonPath("$.attachments[1].id", notNullValue()))
		  .andExpect(jsonPath("$.attachments[1].fileName").value(attachment2.getFileName()))
		  .andExpect(jsonPath("$.attachments[1].size").value(attachment2.getSize()))
		  .andExpect(jsonPath("$.attachments[1].contentType").value(attachment2.getContentType()));
	}

	@Test
	@DisplayName("message 수정 테스트 ")
	void updateMessage() throws Exception {
		// Given
		String newContent = "newContent";
		UpdateMessageRequest request = UpdateMessageRequest.builder()
		  .newContent(newContent)
		  .build();

		Message savedMessage = messageRepository.findById(storedMessage.getId()).get();

		mockMvc.perform(patch("/api/messages/" + storedMessage.getId())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
		  )
		  .andExpect(status().isOk())
		  .andExpect(jsonPath("$.id").value(storedMessage.getId().toString()))
		  .andExpect(jsonPath("$.createdAt", notNullValue()))
		  .andExpect(jsonPath("$.updatedAt", notNullValue()))
		  .andExpect(jsonPath("$.content").value(newContent))
		  .andExpect(jsonPath("$.channelId").value(channel.getId().toString()))
		  .andExpect(jsonPath("$.author.id").value(authorDto.getId().toString()))
		  .andExpect(jsonPath("$.author.username").value(authorDto.getUsername()))
		  .andExpect(jsonPath("$.author.email").value(authorDto.getEmail()));

	}

	@Test
	@DisplayName("message 수정 테스트 - 존재하지 않은 메시지일 때")
	void updateMessageWithNotExistMessage() throws Exception {
		// Given
		String newContent = "newContent";
		UpdateMessageRequest request = UpdateMessageRequest.builder()
		  .newContent(newContent)
		  .build();

		mockMvc.perform(patch("/api/messages/" + UUID.randomUUID())
			.content(objectMapper.writeValueAsString(request))
			.contentType(MediaType.APPLICATION_JSON)
		  )
		  .andExpect(status().isNotFound())
		  .andExpect(jsonPath("$.code").value(MESSAGE_NOT_FOUND.name()))
		  .andExpect(jsonPath("$.message").value(MESSAGE_NOT_FOUND.getMessage()))
		;

	}

	@Test
	@DisplayName("메시지 삭제 테스트 ")
	void deleteMessage() throws Exception {

		mockMvc.perform(delete("/api/messages/" + storedMessage.getId()))
		  .andExpect(status().isNoContent());
	}

}