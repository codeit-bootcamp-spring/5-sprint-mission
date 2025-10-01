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
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.ChannelController;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

@WebMvcTest(controllers = ChannelController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ChannelControllerSliceTest {

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
	private ChannelService channelService;

	@Test
	@DisplayName("ChannelControllerSliceTest - create(PUBLIC) - 성공")
	void create_public_success() throws Exception {

		// 요청
		PublicChannelCreateRequest publicChannelCreateRequest = new PublicChannelCreateRequest("publicChannelName",
			"publicChannelDesc");
		// 응답
		UUID channelId = UUID.randomUUID();
		UserDto userDto = new UserDto(UUID.randomUUID(), "testUserName", "testEmail", null, true);
		ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, publicChannelCreateRequest.name(),
			publicChannelCreateRequest.description(), List.of(userDto),
			Instant.now());

		//
		given(channelService.create(any(PublicChannelCreateRequest.class))).willReturn(channelDto);
		//
		mockMvc.perform(post("/api/channels/public")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(channelDto))
			)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(channelId.toString()))
			.andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.name()))
			.andExpect(jsonPath("$.name").value(publicChannelCreateRequest.name()))
			.andExpect(jsonPath("$.description").value(publicChannelCreateRequest.description()))
			.andExpect(jsonPath("$.participants").exists())
			.andExpect(jsonPath("$.lastMessageAt").exists());
	}

	@Test
	@DisplayName("ChannelControllerSliceTest - create(PRIVATE) - 성공")
	void create_private_success() throws Exception {
		// 응답
		UUID channelId = UUID.randomUUID();
		UserDto userDto = new UserDto(UUID.randomUUID(), "testUserName", "testEmail", null, true);
		ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PRIVATE, null,
			null, List.of(userDto),
			Instant.now());

		//
		given(channelService.create(any(PrivateChannelCreateRequest.class))).willReturn(channelDto);
		//
		mockMvc.perform(post("/api/channels/private")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(channelDto))
			)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(channelId.toString()))
			.andExpect(jsonPath("$.type").value(ChannelType.PRIVATE.name()))
			.andExpect(jsonPath("$.name").doesNotExist())
			.andExpect(jsonPath("$.description").doesNotExist())
			.andExpect(jsonPath("$.participants").exists())
			.andExpect(jsonPath("$.lastMessageAt").exists());
	}

	@Test
	@DisplayName("ChannelControllerSliceTest - findAll - 성공")
	void findAll_success() throws Exception {
		//
		UUID userId1 = UUID.randomUUID();
		UUID userId2 = UUID.randomUUID();

		UserDto userDto1 = new UserDto(userId1, "testUserName", "testEmail", null, true);
		UserDto userDto2 = new UserDto(userId1, "testUserName", "testEmail", null, true);
		UserDto userDto3 = new UserDto(userId2, "testUserName", "testEmail", null, true);
		ChannelDto channelDto1 = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, null,
			null, List.of(userDto2, userDto3),
			Instant.now());
		ChannelDto channelDto2 = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, null,
			null, List.of(userDto1, userDto3),
			Instant.now());
		List<ChannelDto> channelDtos = List.of(channelDto1, channelDto2);

		//
		given(channelService.findAllByUserId(userId2)).willReturn(channelDtos);

		//
		mockMvc.perform(get("/api/channels")
				.param("userId", userId2.toString())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(2));

		verify(channelService, times(1)).findAllByUserId(userId2);
	}

	@Test
	@DisplayName("ChannelControllerSliceTest - findAll - 실패")
	void findAll_fail() throws Exception {
		mockMvc.perform(get("/api/channels").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isInternalServerError());
	}
}
