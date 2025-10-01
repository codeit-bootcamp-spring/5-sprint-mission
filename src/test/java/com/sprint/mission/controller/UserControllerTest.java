package com.sprint.mission.controller;

import static com.sprint.mission.discodeit.exception.ErrorCode.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.UserController;
import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.dto.user.UserResponse;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.request.UserCreateRequest;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
class UserControllerTest {

	@Autowired
	MockMvc mockMvc; // http 요청과 응답을 처리해출 mock 객체

	@Autowired
	private ObjectMapper objectMapper; // json 처리 도와줄 mapper

	@MockitoBean
	private UserService userService;
	@MockitoBean
	private UserMapper userMapper;
	@MockitoBean
	private UserStatusService userStatusService;
	@MockitoBean
	private UserStatusMapper userStatusMapper;
	@MockitoBean
	private JpaMetamodelMappingContext jpaMetamodelMappingContext;

	String username;
	String email;
	String password;

	User user;
	BinaryContent binaryContent;
	BinaryContentDto binaryContentDto;

	byte[] bytes;
	long size;
	String contentType;
	String filename;
	MultipartFile profileRequest;

	@BeforeEach
	void setUp() {
		bytes = "bytes".getBytes();
		size = "size".getBytes().length;
		contentType = "contentType";
		filename = "filename";

		username = "username";
		email = "email";
		password = "password";

		profileRequest = new MockMultipartFile(
		  "file",               // form field name (컨트롤러 @RequestParam("file")와 매칭)
		  filename,             // original filename
		  contentType,          // content type
		  bytes                 // file content
		);

		binaryContent = BinaryContent.builder()
		  .id(UUID.randomUUID())
		  .fileName(filename)
		  .contentType(contentType)
		  .size(size)
		  .build();
		binaryContentDto = BinaryContentDto.builder()
		  .id(binaryContent.getId())
		  .fileName(filename)
		  .size(size)
		  .contentType(contentType)
		  .build();

		user = User.builder()
		  .id(UUID.randomUUID())
		  .username(username)
		  .email(email)
		  .password(password)
		  .profileImage(binaryContent)
		  .build();
	}

	@Test
	@DisplayName("createUser 테스트 프로필이 있을 때")
	void createUser() throws Exception {
		// Given
		UserCreateRequest request = UserCreateRequest.builder()
		  .username(username)
		  .email(email)
		  .password(password)
		  .build();

		UserDto dto = UserDto.builder()
		  .id(user.getId())
		  .username(username)
		  .email(email)
		  .profile(binaryContentDto)
		  .online(true)
		  .build();

		UserResponse mockResponse = UserResponse.builder()
		  .id(user.getId())
		  .username(username)
		  .email(email)
		  .profile(binaryContentDto)
		  .online(true)
		  .build();

		given(userService.create(any())).willReturn(dto);
		given(userMapper.toResponse(any())).willReturn(mockResponse);

		// JSON part
		MockMultipartFile jsonPart = new MockMultipartFile(
		  "userCreateRequest",       // 컨트롤러의 @RequestPart 이름
		  "userCreateRequest.json",
		  "application/json",
		  objectMapper.writeValueAsBytes(request)
		);

		// 파일 part
		MockMultipartFile profilePart = new MockMultipartFile(
		  "profile",                 // 컨트롤러의 @RequestPart 이름
		  "profile.png",
		  "image/png",
		  "test image".getBytes()
		);

		// When & Then
		mockMvc.perform(multipart("/api/users")
			.file(jsonPart)
			.file(profilePart))
		  .andExpect(status().isCreated())
		  .andExpect(jsonPath("$.id").value(user.getId().toString()))
		  .andExpect(jsonPath("$.email").value(email))
		  .andExpect(jsonPath("$.profile.id").value(binaryContent.getId().toString()))
		  .andExpect(jsonPath("$.profile.fileName").value(filename))
		  .andExpect(jsonPath("$.profile.size").value(size))
		  .andExpect(jsonPath("$.profile.contentType").value(contentType))
		  .andExpect(jsonPath("$.email").value(email))
		  .andExpect(jsonPath("$.online").value(dto.getOnline()));

	}

	@Test
	@DisplayName("createUser 테스트 비어있는 username이 있을 때")
	void createUserWithBlankInput() throws Exception {
		UserCreateRequest request = UserCreateRequest.builder()
		  .username("")
		  .email(email)
		  .password(password)
		  .build();
		// JSON part
		MockMultipartFile jsonPart = new MockMultipartFile(
		  "userCreateRequest",       // 컨트롤러의 @RequestPart 이름
		  "userCreateRequest.json",
		  "application/json",
		  objectMapper.writeValueAsBytes(request)
		);

		mockMvc.perform(multipart("/api/users")
			.file(jsonPart))
		  .andExpect(status().isBadRequest())
		  .andExpect(jsonPath("$.code").value(VALIDATION_ERROR.name()))
		  .andExpect(jsonPath("$.message").value(VALIDATION_ERROR.getMessage()));

	}

	@Test
	@DisplayName("getAllUser 테스트")
	void getAllUser() throws Exception {
		UserCreateRequest request = UserCreateRequest.builder()
		  .username(username)
		  .email(email)
		  .password(password)
		  .build();

		UserDto dto1 = UserDto.builder()
		  .id(user.getId())
		  .username(username)
		  .email(email)
		  .profile(binaryContentDto)
		  .online(true)
		  .build();

		UserDto dto2 = UserDto.builder()
		  .id(UUID.randomUUID())
		  .username(username + "2")
		  .email(email + "2")
		  .profile(null)
		  .online(false)
		  .build();

		UserResponse response1 = UserResponse.builder()
		  .id(user.getId())
		  .username(user.getUsername())
		  .email(user.getEmail())
		  .profile(dto1.getProfile())
		  .online(dto1.getOnline())
		  .build();
		UserResponse response2 = UserResponse.builder()
		  .id(dto2.getId())
		  .username(dto2.getUsername())
		  .email(dto2.getEmail())
		  .profile(dto2.getProfile())
		  .online(dto2.getOnline())
		  .build();

		given(userService.readAll()).willReturn(List.of(dto1, dto2));
		given(userMapper.toResponse(dto1)).willReturn(response1);
		given(userMapper.toResponse(dto2)).willReturn(response2);

		// When & Then
		mockMvc.perform(get("/api/users"))
		  .andExpect(status().isOk())
		  .andExpect(jsonPath("$[0].id").value(dto1.getId().toString()))
		  .andExpect(jsonPath("$[0].username").value(dto1.getUsername()))
		  .andExpect(jsonPath("$[0].email").value(dto1.getEmail()))
		  .andExpect(jsonPath("$[0].profile.id").value(binaryContentDto.getId().toString()))
		  .andExpect(jsonPath("$[0].profile.fileName").value(filename))
		  .andExpect(jsonPath("$[0].profile.size").value(size))
		  .andExpect(jsonPath("$[0].profile.contentType").value(contentType))
		  .andExpect(jsonPath("$[0].online").value(dto1.getOnline()))
		  .andExpect(jsonPath("$[1].id").value(dto2.getId().toString()))
		  .andExpect(jsonPath("$[1].username").value(dto2.getUsername()))
		  .andExpect(jsonPath("$[1].email").value(dto2.getEmail()))
		  .andExpect(jsonPath("$[1].online").value(dto2.getOnline()));

	}

	@Test
	@DisplayName("updateUser 테스트")
	void updateUser() {
	}

	@Test
	@DisplayName("deleteUser 테스트")
	void deleteUser() {
	}

	@Test
	@DisplayName("updateUserStatus 테스트")
	void updateUserStatus() {
	}
}