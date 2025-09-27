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
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ChannelControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ChannelService channelService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;

	@Test
	@DisplayName("ChannelControllerIntegrationTest - create(PUBLIC) - 성공")
	public void createPublic_success() throws Exception {
		PublicChannelCreateRequest publicChannelCreateRequest = new PublicChannelCreateRequest("testName",
			"testDescription");

		mockMvc.perform(
			post("/api/channels/public")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(publicChannelCreateRequest))
		)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name", is("testName")))
			.andExpect(jsonPath("$.description", is("testDescription")));

	}
	@Test
	@DisplayName("ChannelControllerIntegrationTest - create(PRIVATE) - 성공")
	public void createPrivate_success() throws Exception {

		UserCreateRequest userCreateRequest = new UserCreateRequest("username", "email@email", "1234");
		UserDto userDto = userService.create(userCreateRequest, Optional.of(new BinaryContentCreateRequest("filename", "jpg", "da".getBytes())));

		PrivateChannelCreateRequest privateChannelCreateRequest = new PrivateChannelCreateRequest(
			List.of(userDto.id()));

		mockMvc.perform(
			post("/api/channels/private")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(privateChannelCreateRequest))
		)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").isNotEmpty())
			.andExpect(jsonPath("$.participants[0].id").value(userDto.id().toString()));
	}

	@Test
	@DisplayName("UserControllerIntegrationTest - update - 성공 (프로필 포함)")
	public void update_success() throws Exception {
		UserDto created = userService.create(
			new UserCreateRequest("before", "before@before", "before!1234"),
			Optional.empty()
		);
		UUID userId = created.id();

		UserUpdateRequest updateReq = new UserUpdateRequest(
			"after", "after@after", "after!1234"
		);
		MockMultipartFile updatePart = new MockMultipartFile(
			"userUpdateRequest",
			"userUpdateRequest.json",
			MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsBytes(updateReq)
		);

		MockMultipartFile profilePart = new MockMultipartFile(
			"profile",
			"profile.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"image".getBytes()
		);

		//
		mockMvc.perform(
				multipart("/api/users/" + userId)
					.file(updatePart)
					.file(profilePart)
					.characterEncoding("UTF-8")
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.accept(MediaType.APPLICATION_JSON)
					.with(request->{
						request.setMethod("PATCH");
						return  request;
					})
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(userId.toString()))
			.andExpect(jsonPath("$.username").value("after"))
			.andExpect(jsonPath("$.email").value("after@after"))
			.andExpect(jsonPath("$.profile").exists());
	}

	@Test
	@DisplayName("ChannelControllerIntegrationTest - delete - 성공")
	public void delete_success() throws Exception {
		UserDto created = userService.create(
			new UserCreateRequest("delete", "delete@delete", "delete!1234"),
			Optional.empty()
		);
		UUID userId = created.id();

		//
		mockMvc.perform(delete("/api/users/{id}", userId)
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isNoContent());


		boolean exists = userRepository.existsById(userId);
		assertThat(exists).isFalse();
	}

}
