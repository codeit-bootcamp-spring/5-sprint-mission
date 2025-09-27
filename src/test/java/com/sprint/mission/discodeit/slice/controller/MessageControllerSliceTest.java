package com.sprint.mission.discodeit.slice.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.MessageController;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

@WebMvcTest(controllers = MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MessageControllerSliceTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private UserStatusService userStatusService;

	@MockitoBean
	private JpaMetamodelMappingContext jpaMetamodelMappingContext;

	@MockitoBean
	private MessageService messageService;

	@Test
	@DisplayName("MessageControllerSliceTest - create - 성공(첨부물 있음)")
	void create_success() throws Exception {
		//
		UUID channelId = UUID.randomUUID();
		UUID authorId = UUID.randomUUID();
		String content = "test";
		MessageCreateRequest req = new MessageCreateRequest(content, channelId, authorId);

		MockMultipartFile jsonPart = new MockMultipartFile(
			"messageCreateRequest", "request.json",
			MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsBytes(req)
		);

		MockMultipartFile att1 = new MockMultipartFile(
			"attachments", "a.jpg", MediaType.IMAGE_JPEG_VALUE, "img1".getBytes()
		);
		MockMultipartFile att2 = new MockMultipartFile(
			"attachments", "b.png", MediaType.IMAGE_PNG_VALUE, "img2".getBytes()
		);

		UUID messageId = UUID.randomUUID();
		UserDto author = new UserDto(authorId, "username", "email@email", null, true);
		MessageDto res = new MessageDto(messageId, null, null, content, channelId, author, null);

		//
		given(messageService.create(any(MessageCreateRequest.class), any(List.class)))
			.willReturn(res);

		//
		mockMvc.perform(multipart("/api/messages")
				.file(jsonPart)
				.file(att1)
				.file(att2)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(messageId.toString()))
			.andExpect(jsonPath("$.content").value(content));
	}

	@Test
	@DisplayName("MessageControllerSliceTest - create - 성공(첨부물없음)")
	void create_success_withoutAttachments() throws Exception {
		UUID channelId = UUID.randomUUID();
		UUID authorId = UUID.randomUUID();
		String content = "hello";
		MessageCreateRequest req = new MessageCreateRequest(content, channelId, authorId);

		MockMultipartFile jsonPart = new MockMultipartFile(
			"messageCreateRequest", "request.json",
			MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsBytes(req)
		);

		UUID messageId = UUID.randomUUID();
		UserDto author = new UserDto(authorId, "username", "email@email", null, true);
		MessageDto res = new MessageDto(messageId, null, null, content, channelId, author, null);

		//
		given(messageService.create(any(MessageCreateRequest.class), any(List.class)))
			.willReturn(res);
		//
		mockMvc.perform(multipart("/api/messages")
				.file(jsonPart)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(messageId.toString()))
			.andExpect(jsonPath("$.content").value(content));
	}

	@Test
	@DisplayName("MessageControllerSliceTest - findAllByChannelId - 성공(기본값)")
	void findAllByChannelId_success_withoutCursor() throws Exception {
		//
		UUID channelId = UUID.randomUUID();

		UserDto author = new UserDto(UUID.randomUUID(), "user1", "email@email", null, true);
		MessageDto messageDto1 = new MessageDto(UUID.randomUUID(), null, null, "content1", channelId, author, null);
		MessageDto messageDto2 = new MessageDto(UUID.randomUUID(), null, null, "content2", channelId, author, null);

		PageResponse<MessageDto> page =
			new PageResponse<>(List.of(messageDto1, messageDto2), "2025-09-27T00:00:00Z", 50, true, 123L
			);
		//
		given(messageService.findAllByChannelId(eq(channelId), isNull(), any(Pageable.class))).willReturn(page);
		//
		mockMvc.perform(get("/api/messages")
				.param("channelId", channelId.toString())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content.length()").value(2))
			.andExpect(jsonPath("$.content[0].id").value(messageDto1.id().toString()))
			.andExpect(jsonPath("$.content[0].content").value("content1"))
			.andExpect(jsonPath("$.nextCursor").value("2025-09-27T00:00:00Z"))
			.andExpect(jsonPath("$.size").value(50))
			.andExpect(jsonPath("$.hasNext").value(true))
			.andExpect(jsonPath("$.totalElements").value(123));
	}

	@Test
	@DisplayName("MessageControllerSliceTest - findAllByChannelId - 성공(정렬 값 지정)")
	void findAllByChannelId_success_withCursor() throws Exception {
		UUID channelId = UUID.randomUUID();
		Instant cursor = Instant.parse("2025-09-26T23:00:00Z");

		UserDto author = new UserDto(UUID.randomUUID(), "user2", "test@test", null, true);
		MessageDto m1 = new MessageDto(UUID.randomUUID(), null, null, "content", channelId, author, null);

		PageResponse<MessageDto> page =
			new PageResponse<>(List.of(m1), null, 10, false, 1L);

		given(messageService.findAllByChannelId(eq(channelId), eq(cursor), any(Pageable.class)))
			.willReturn(page);

		mockMvc.perform(get("/api/messages")
				.param("channelId", channelId.toString())
				.param("cursor", "2025-09-26T23:00:00Z")
				.param("page", "1")
				.param("size", "10")
				.param("sort", "createdAt,ASC")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(1))
			.andExpect(jsonPath("$.nextCursor").doesNotExist())
			.andExpect(jsonPath("$.size").value(10))
			.andExpect(jsonPath("$.hasNext").value(false))
			.andExpect(jsonPath("$.totalElements").value(1));

	}

}
