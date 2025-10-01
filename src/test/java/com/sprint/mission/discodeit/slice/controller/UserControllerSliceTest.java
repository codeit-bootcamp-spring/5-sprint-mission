package com.sprint.mission.discodeit.slice.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.UserController;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerSliceTest {

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

	@Test
	@DisplayName("UserControllerSlice - create - 성공 ")
	void createUser_success() throws Exception {
		// 요청
		UserCreateRequest userCreateRequest = new UserCreateRequest("testUserName", "testEmail@gmail.com",
			"qlalfqjsgh!123");

		MockMultipartFile jsonFile = new MockMultipartFile(
			"userCreateRequest",
			"",
			MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsBytes(userCreateRequest)
		);
		MockMultipartFile profileFile = new MockMultipartFile(
			"profile",
			"profile.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"test image content".getBytes()
		);

		// 응답
		BinaryContentDto binaryContentDto = new BinaryContentDto(
			UUID.randomUUID(),
			profileFile.getOriginalFilename(),
			(long)profileFile.getBytes().length,
			profileFile.getContentType()
		);

		UserDto userDto = new UserDto(
			UUID.randomUUID(),
			userCreateRequest.username(),
			userCreateRequest.email(),
			binaryContentDto,
			true);

		//
		given(userService.create(any(UserCreateRequest.class), any(Optional.class))).willReturn(userDto);

		//
		mockMvc.perform(multipart("/api/users")
				.file(jsonFile)
				.file(profileFile)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.username").value("testUserName"))
			.andExpect(jsonPath("$.email").value("testEmail@gmail.com"))
			.andExpect(jsonPath("$.profile").exists())
			.andExpect(jsonPath("$.online").value(true));
	}

	@Test
	@DisplayName("UserControllerSlice - create - 실패: 잘못된 이메일 형식")
	void createUser_fail() throws Exception {
		// 요청
		UserCreateRequest userCreateRequest = new UserCreateRequest("testUserName", "testEmail", "qlalfqjsgh!123");

		MockMultipartFile jsonFile = new MockMultipartFile(
			"userCreateRequest",
			"",
			MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsBytes(userCreateRequest)
		);
		MockMultipartFile profileFile = new MockMultipartFile(
			"profile",
			"profile.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"test image content".getBytes()
		);

		// 응답
		BinaryContentDto binaryContentDto = new BinaryContentDto(
			UUID.randomUUID(),
			profileFile.getOriginalFilename(),
			(long)profileFile.getBytes().length,
			profileFile.getContentType()
		);

		UserDto userDto = new UserDto(
			UUID.randomUUID(),
			userCreateRequest.username(),
			userCreateRequest.email(),
			binaryContentDto,
			true);

		//
		given(userService.create(any(UserCreateRequest.class), any(Optional.class))).willReturn(userDto);

		//
		mockMvc.perform(multipart("/api/users")
				.file(jsonFile)
				.file(profileFile)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.message").value("요청 데이터 유효성 검사에 실패했습니다"))
			.andExpect(jsonPath("$.details").exists())
			.andExpect(jsonPath("$.exceptionType").exists())
			.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
	}

	@Test
	@DisplayName("UserController - update - 성공")
	void updateUser_success() throws Exception {
		UUID userId = UUID.randomUUID();

		// 요청
		UserUpdateRequest req = new UserUpdateRequest(
			"newName", "newEmail@gmail.com", "newPassword!123"
		);

		MockMultipartFile jsonPart = new MockMultipartFile(
			"userUpdateRequest",
			"request.json",
			MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsBytes(req)
		);

		MockMultipartFile profilePart = new MockMultipartFile(
			"profile",
			"new-profile.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"binary".getBytes()
		);

		// 응답
		BinaryContentDto profileDto = new BinaryContentDto(
			UUID.randomUUID(), "new-profile.jpg", 6L, MediaType.IMAGE_JPEG_VALUE
		);
		UserDto res = new UserDto(userId, "newName", "newEmail@gmail.com", profileDto, true);
		//
		given(userService.update(eq(userId), any(UserUpdateRequest.class), any())).willReturn(res);

		//
		mockMvc.perform(
				multipart("/api/users/{userId}", userId)
					.file(jsonPart)
					.file(profilePart)
					.with(req1 -> {
						req1.setMethod("PATCH");
						return req1;
					}) // 핵심
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(userId.toString()))
			.andExpect(jsonPath("$.username").value("newName"))
			.andExpect(jsonPath("$.email").value("newEmail@gmail.com"))
			.andExpect(jsonPath("$.profile").exists())
			.andExpect(jsonPath("$.online").value(true));
	}

	@Test
	@DisplayName("UserController - update - 실패: json 잘못줌")
	void updateUser_fail() throws Exception {
		UUID userId = UUID.randomUUID();

		MockMultipartFile profilePart = new MockMultipartFile(
			"profile", "p.jpg", MediaType.IMAGE_JPEG_VALUE, "img".getBytes()
		);

		mockMvc.perform(
				multipart("/api/users/{userId}", userId)
					.file(profilePart)
					.with(req1 -> {
						req1.setMethod("PATCH");
						return req1;
					})
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.code").exists())
			.andExpect(jsonPath("$.message").exists())
			.andExpect(jsonPath("$.details").exists())
			.andExpect(jsonPath("$.exceptionType").exists())
			.andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()));
	}

}
