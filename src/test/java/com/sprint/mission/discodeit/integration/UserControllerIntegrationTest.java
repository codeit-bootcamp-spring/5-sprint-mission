package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserService userService;

	@Test
	@DisplayName("UserControllerIntegrationTest - create - 성공")
	public void createUser_success() throws Exception {
		//
		UserCreateRequest userCreateRequest = new UserCreateRequest(
			"test", "test@test", "test!123"
		);
		MockMultipartFile userCreatePart = new MockMultipartFile(
			"userCreateRequest",
			"userCreateRequest.json",
			MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsBytes(userCreateRequest)
		);

		byte[] imageBytes = "imageContent".getBytes();
		MockMultipartFile profilePart = new MockMultipartFile(
			"profile",
			"test.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			imageBytes
		);

		//
		mockMvc.perform(
				multipart("/api/users")
					.file(userCreatePart)
					.file(profilePart)
					.characterEncoding("UTF-8")
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isCreated())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.username").value("test"))
			.andExpect(jsonPath("$.email").value("test@test"))
			.andExpect(jsonPath("$.profile").exists());
	}

	@Test
	@DisplayName("UserControllerIntegrationTest - update - 성공")
	public void updateUser_success() throws Exception {
		UserDto beforeUserDto = userService.create(
			new UserCreateRequest("before", "before@before", "before!123"),
			Optional.empty()
		);
		UUID userId = beforeUserDto.id();

		UserUpdateRequest updateRequest = new UserUpdateRequest(
			"after", "after@after", "after!123"
		);

		MockMultipartFile updatePart = new MockMultipartFile(
			"userUpdateRequest",
			"userUpdateRequest.json",
			MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsBytes(updateRequest)
		);

		MockMultipartFile profilePart = new MockMultipartFile(
			"profile",
			"profile.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"imageContent".getBytes()
		);

		//
		mockMvc.perform(
				multipart("/api/users" + "/" + userId)
					.file(updatePart)
					.file(profilePart)
					.characterEncoding("UTF-8")
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.accept(MediaType.APPLICATION_JSON)
					.with(request -> {
						request.setMethod("PATCH");
						return request;
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
	@DisplayName("UserControllerIntegrationTest - delete - 성공")
	public void delete_success() throws Exception {
		UserDto beforeUserDto2 = userService.create(
			new UserCreateRequest("before", "before@before", "before!123"),
			Optional.empty()
		);

		UUID deleteUserId = beforeUserDto2.id();

		mockMvc.perform(
			delete("/api/users" + "/" + deleteUserId)
		).andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("UserControllerIntegrationTest - findAll - 성공")
	public void findAll_success() throws Exception {
		UserDto beforeUserDto1 = userService.create(
			new UserCreateRequest("before", "before@before", "before!123"),
			Optional.empty()
		);
		UserDto beforeUserDto2 = userService.create(
			new UserCreateRequest("before1", "before1@before", "before!123"),
			Optional.empty()
		);

		mockMvc.perform(
				get("/api/users")
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
	}

}
