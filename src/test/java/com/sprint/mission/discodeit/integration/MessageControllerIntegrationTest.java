package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MessageControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ChannelService channelService;

	@Autowired
	private MessageService messageService;

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private UserService userService;

	@Test
	@DisplayName("MessageControllerIntegrationTest - create - 성공")
	void create_success() throws Exception {

		UserDto userDto = userService.create(new UserCreateRequest("name", "email@email", "pw!123"), Optional.empty());
		UUID userId = userDto.id();
		ChannelDto channelDto = channelService.create(new PublicChannelCreateRequest("chName", "chDesc"));
		UUID channelId = channelDto.id();

		MessageCreateRequest messageCreateRequest = new MessageCreateRequest("testContent", channelId, userId);

		MockMultipartFile jsonPart = new MockMultipartFile(
			"messageCreateRequest", "request.json",
			org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsBytes(messageCreateRequest)
		);
		MockMultipartFile att1 = new MockMultipartFile(
			"attachments", "a.jpg", org.springframework.http.MediaType.IMAGE_JPEG_VALUE, "img1".getBytes()
		);
		MockMultipartFile att2 = new MockMultipartFile(
			"attachments", "b.png", MediaType.IMAGE_PNG_VALUE, "img2".getBytes()
		);

		mockMvc.perform(
				multipart("/api/messages")
					.file(jsonPart)
					.file(att1)
					.file(att2)
					.characterEncoding("UTF-8")
					.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").isNotEmpty())
			.andExpect(jsonPath("$.content").value("testContent"))
			.andExpect(jsonPath("$.author.id").value(userId.toString()));
	}

	@Test
	@DisplayName("MessageControllerIntegrationTest - update - 성공")
	void update_success() throws Exception {
		//
		UserDto author = userService.create(new UserCreateRequest("username", "email@email", "pw!1234"),
			Optional.empty());
		ChannelDto channel = channelService.create(new PublicChannelCreateRequest("chName", "chDesc"));
		MessageDto created = messageService.create(new MessageCreateRequest("before", channel.id(), author.id()),
			List.of());

		MessageUpdateRequest updateReq = new MessageUpdateRequest("after");
		String body = objectMapper.writeValueAsString(updateReq);

		//
		mockMvc.perform(
				patch("/api/messages/{id}", created.id())
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(body)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(created.id().toString()))
			.andExpect(jsonPath("$.content").value("after"))
			.andExpect(jsonPath("$.author.id").value(author.id().toString()))
			.andExpect(jsonPath("$.channelId").value(channel.id().toString()));
	}

	@Test
	@DisplayName("MessageControllerIntegrationTest - delete - 성공")
	void delete_success() throws Exception {
		UserDto userDto = userService.create(new UserCreateRequest("username", "email@email", "pw!1234"),
			Optional.empty());
		ChannelDto channelDto = channelService.create(new PublicChannelCreateRequest("chName", "chDesc"));
		MessageDto messageDto = messageService.create(new MessageCreateRequest("before", channelDto.id(), userDto.id()),
			List.of());
		//
		mockMvc.perform(
				delete("/api/messages/{id}", messageDto.id())
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isNoContent());
		assertThat(messageRepository.existsById(messageDto.id())).isFalse();
	}

	@Test
	@DisplayName("MessageControllerIntegrationTest - findAllByChannelId - 성공")
	void findAllByChannelId_success() throws Exception {
		UserDto user = userService.create(new UserCreateRequest("user", "user@user", "user!1234"), Optional.empty());
		ChannelDto channel = channelService.create(new PublicChannelCreateRequest("publicChannel", "chDesc"));

		MessageDto m1 = messageService.create(new MessageCreateRequest("m1", channel.id(), user.id()), List.of());
		MessageDto m2 = messageService.create(new MessageCreateRequest("m2", channel.id(), user.id()), List.of());
		MessageDto m3 = messageService.create(new MessageCreateRequest("m3", channel.id(), user.id()), List.of());

		//
		MvcResult page1 = mockMvc.perform(
				get("/api/messages")
					.param("channelId", channel.id().toString())
					.param("size", "2")
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content", hasSize(2)))
			.andExpect(jsonPath("$.content[0].content").value("m3"))
			.andExpect(jsonPath("$.content[1].content").value("m2"))
			.andExpect(jsonPath("$.size").value(2))
			.andExpect(jsonPath("$.hasNext").value(true))
			.andReturn();
	}
}
