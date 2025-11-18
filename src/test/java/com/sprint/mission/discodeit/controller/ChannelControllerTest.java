package com.sprint.mission.discodeit.controller;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;

@ActiveProfiles("test")
@WebMvcTest(controllers = ChannelController.class)
@Import({GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
public class ChannelControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private JpaMetamodelMappingContext mappingContext;

	@MockitoBean
	private ChannelService channelService;

	// POST /api/channels/public

	@Test
	@DisplayName("create public - 성공 시 201과 ChannelDto 반환")
	void createPublic_success_createsChannel201() throws Exception {
		PublicChannelCreateRequest request =
			new PublicChannelCreateRequest("test", "description");
		ChannelDto channelDto = new ChannelDto(
			UUID.randomUUID(),
			ChannelType.PUBLIC,
			request.name(),
			request.description(),
			List.of(),
			null
		);
		String requestBody = objectMapper.writeValueAsString(request);

		given(channelService.create(any(PublicChannelCreateRequest.class))).willReturn(channelDto);

		mockMvc.perform(
				post("/api/channels/public")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.name").value(request.name()))
			.andExpect(jsonPath("$.description").value(request.description()))
			.andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.name()));
	}

	@Test
	@DisplayName("create public - DTO 유효성 검증 실패 시 400")
	void createPublic_validationError_returns400() throws Exception {
		PublicChannelCreateRequest invalid =
			new PublicChannelCreateRequest("", "a".repeat(501));

		String requestBody = objectMapper.writeValueAsString(invalid);

		mockMvc.perform(
				post("/api/channels/public")
					.content(requestBody)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());

		then(channelService).shouldHaveNoInteractions();
	}

	// POST /api/channels/private

	@Test
	@DisplayName("create private - 성공 시 201과 ChannelDto 반환")
	void createPrivate_success_createsChannel201() throws Exception {
		PrivateChannelCreateRequest request =
			new PrivateChannelCreateRequest(List.of(UUID.randomUUID()));
		UserDto userDto = new UserDto(
			request.participantIds().get(0),
			"test",
			"test@email.com",
			null,
			false,
			Role.USER
		);
		ChannelDto channelDto = new ChannelDto(
			UUID.randomUUID(),
			ChannelType.PRIVATE,
			null,
			null,
			List.of(userDto),
			null
		);
		String requestBody = objectMapper.writeValueAsString(request);

		given(channelService.create(any(PrivateChannelCreateRequest.class))).willReturn(channelDto);

		mockMvc.perform(
				post("/api/channels/private")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.description").isEmpty())
			.andExpect(jsonPath("$.name").isEmpty())
			.andExpect(jsonPath("$.type").value(ChannelType.PRIVATE.name()))
			.andExpect(jsonPath("$.participants").isNotEmpty());
	}

	@Test
	@DisplayName("create private - 찾을 수 없는 유저 404(NOT_FOUND)")
	void createPrivate_userNotFound_returns404() throws Exception {
		UUID userId = UUID.randomUUID();
		PrivateChannelCreateRequest request =
			new PrivateChannelCreateRequest(List.of(userId));
		String requestBody = objectMapper.writeValueAsString(request);

		DiscodeitException ex = new UserNotFoundException().addDetail("userId", userId);
		given(channelService.create(any(PrivateChannelCreateRequest.class))).willThrow(ex);

		mockMvc.perform(
				post("/api/channels/private")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody))
			.andExpect(status().isNotFound())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(ex.getErrorCode().name()))
			.andExpect(jsonPath("$.message").value(ex.getErrorCode().getMessage()))
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.exceptionType").value(ex.getClass().getSimpleName()))
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.details.userId").value(userId.toString()));
	}

	// PATCH /api/channels/{channelId}

	@Test
	@DisplayName("update - 성공 시 200과 갱신된 ChannelDto 반환")
	void update_success_updatesChannel200() throws Exception {
		UUID channelId = UUID.randomUUID();
		PublicChannelUpdateRequest request =
			new PublicChannelUpdateRequest("updated", "updated");
		ChannelDto channelDto = new ChannelDto(
			channelId,
			ChannelType.PUBLIC,
			request.newName(),
			request.newDescription(),
			List.of(),
			null
		);
		String requestBody = objectMapper.writeValueAsString(request);
		given(channelService.update(any(), any())).willReturn(channelDto);

		mockMvc.perform(
				patch("/api/channels/{channelId}", channelId)
					.content(requestBody)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.name").value(channelDto.name()))
			.andExpect(jsonPath("$.description").value(channelDto.description()))
			.andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.name()))
			.andExpect(jsonPath("$.participants").isEmpty());
	}

	@Test
	@DisplayName("update - 채널이 없으면 404(NOT_FOUND)")
	void update_channelNotFound_returns404() throws Exception {
		UUID channelId = UUID.randomUUID();
		PublicChannelUpdateRequest request =
			new PublicChannelUpdateRequest("updated", "updated");

		String requestBody = objectMapper.writeValueAsString(request);
		DiscodeitException ex = new ChannelNotFoundException().addDetail("channelId", channelId);

		given(channelService.update(any(), any())).willThrow(ex);

		mockMvc.perform(
				patch("/api/channels/{channelId}", channelId)
					.content(requestBody)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(ex.getErrorCode().name()))
			.andExpect(jsonPath("$.message").value(ex.getErrorCode().getMessage()))
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.exceptionType").value(ex.getClass().getSimpleName()))
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.details.channelId").value(channelId.toString()));
	}

	// DELETE /api/channels/{channelId}

	@Test
	@DisplayName("delete - 성공 시 204(NO_CONTENT)")
	void delete_success_returns204() throws Exception {
		UUID channelId = UUID.randomUUID();

		willDoNothing().given(channelService).delete(channelId);

		mockMvc.perform(
				delete("/api/channels/{channelId}", channelId))
			.andExpect(status().isNoContent());

		then(channelService).should().delete(channelId);
	}

	@Test
	@DisplayName("delete - 채널이 없으면 404(NOT_FOUND)")
	void delete_channelNotFound_returns404() throws Exception {
		UUID channelId = UUID.randomUUID();

		DiscodeitException ex = new ChannelNotFoundException().addDetail("channelId", channelId);
		willThrow(ex).given(channelService).delete(channelId);

		mockMvc.perform(
				delete("/api/channels/{channelId}", channelId)
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(ex.getErrorCode().name()))
			.andExpect(jsonPath("$.message").value(ex.getErrorCode().getMessage()))
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.exceptionType").value(ex.getClass().getSimpleName()))
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.details.channelId").value(channelId.toString()));
	}

	// GET /api/channels?userId=...
	@Test
	@DisplayName("findAllByUserId - 성공 시 200과 채널 리스트 반환")
	void findAllByUserId_success_returns200List() throws Exception {
		UUID userId = UUID.randomUUID();
		UserDto userDto = new UserDto(
			userId,
			"test1",
			"test1@email.com",
			null,
			false,
			Role.USER
		);
		List<ChannelDto> channelDtos = List.of(
			new ChannelDto(
				UUID.randomUUID(),
				ChannelType.PUBLIC,
				"test1",
				"test1",
				List.of(),
				null
			),
			new ChannelDto(
				UUID.randomUUID(),
				ChannelType.PRIVATE,
				"test2",
				"test2",
				List.of(userDto),
				null
			));
		given(channelService.findAllByUserId(any())).willReturn(channelDtos);

		mockMvc.perform(
				get("/api/channels")
					.queryParam("userId", userId.toString())
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].id", notNullValue()))
			.andExpect(jsonPath("$[0].name").value("test1"))
			.andExpect(jsonPath("$[0].description").value("test1"))
			.andExpect(jsonPath("$[1].name").value("test2"))
			.andExpect(jsonPath("$[1].type").value(ChannelType.PRIVATE.name()));
	}

	@Test
	@DisplayName("findAllByUserId - userId 누락/형식오류 시 500(INTERNAL_SERVER_ERROR)")
	void findAllByUserId_missingOrInvalidUserId_returns500() throws Exception {
		mockMvc.perform(get("/api/channels")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isInternalServerError())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value(500));

		then(channelService).shouldHaveNoInteractions();
	}
}
