package com.sprint.mission.integration;

import static com.sprint.mission.discodeit.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.domain.dto.CreateBiContentDTO;
import com.sprint.mission.discodeit.domain.dto.CreateUserDTO;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.request.UserCreateRequest;
import com.sprint.mission.discodeit.domain.request.UserUpdateRequest;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserApiTest {

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	MockMvc mockMvc;
	@Autowired
	UserRepository userRepository;

	@MockitoBean
	private S3Client s3Client;
	@MockitoBean
	private S3Presigner s3Presigner;

	String username;
	String email;
	String password;

	byte[] bytes;
	long size;
	String contentType;
	String filename;
	MultipartFile profileRequest;
	@Autowired
	private BasicUserService basicUserService;
	@Autowired
	private BinaryContentRepository binaryContentRepository;
	@Autowired
	private BasicBinaryContentService basicBinaryContentService;

	@BeforeEach
	void setUp() {
		bytes = "bytes".getBytes();
		size = "size".getBytes().length;
		contentType = MediaType.IMAGE_PNG_VALUE;
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

	}

	@Test
	@DisplayName("사용자 생성 API 테스트 올바른 값이 주어졌을 때")
	void createUserApiTest() throws Exception {
		UserCreateRequest request = UserCreateRequest.builder()
		  .username(username)
		  .email(email)
		  .password(password)
		  .build();

		MockMultipartFile userPart = new MockMultipartFile(
		  "userCreateRequest",
		  "userCreateRequest.json",
		  MediaType.APPLICATION_JSON_VALUE,
		  objectMapper.writeValueAsBytes(request)
		);

		MockMultipartFile avatarPart = new MockMultipartFile(
		  "profile",
		  filename,
		  contentType,
		  bytes
		);

		mockMvc.perform(multipart("/api/users")
			.file(userPart)
			.file(avatarPart)
			.contentType(MediaType.MULTIPART_FORM_DATA))
		  .andExpect(status().isCreated())
		  .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		  // ApiResult 래핑
		  .andExpect(jsonPath("$.id", notNullValue()))
		  .andExpect(jsonPath("$.username").value(username))
		  .andExpect(jsonPath("$.email").value(email))
		  .andExpect(jsonPath("$.profile.fileName").value(filename))
		  .andExpect(jsonPath("$.profile.size").value(bytes.length))
		  .andExpect(jsonPath("$.profile.contentType").value(contentType));
	}

	@Test
	@DisplayName("사용자 생성 API 테스트 이미 존재하는 이메일 값이 주어졌을 때")
	void createUserApiTestDuplicateEmail() throws Exception {
		userRepository.save(User.builder()
		  .username(username + "1")
		  .password(password + "1")
		  .email(email)
		  .build());

		UserCreateRequest request = UserCreateRequest.builder()
		  .username(username)
		  .email(email)
		  .password(password)
		  .build();

		MockMultipartFile userPart = new MockMultipartFile(
		  "userCreateRequest",
		  "userCreateRequest.json",
		  MediaType.APPLICATION_JSON_VALUE,
		  objectMapper.writeValueAsBytes(request)
		);

		mockMvc.perform(multipart("/api/users")
			.file(userPart)
			.contentType(MediaType.MULTIPART_FORM_DATA))
		  .andExpect(status().isBadRequest())
		  .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		  .andExpect(jsonPath("$.code").value(DUPLICATE_USERNAME_OR_EMAIL.name()))
		  .andExpect(jsonPath("$.message").value(DUPLICATE_USERNAME_OR_EMAIL.getMessage()));
	}

	@Test
	@DisplayName("사용자 목록 조회 테스트")
	void getAllUserApiTest() throws Exception {

		for (int i = 0; i < 3; i++) {
			User user = User.builder()
			  .username(username + i)
			  .password(password + i)
			  .email(email + i)
			  .build();

			userRepository.save(user);

		}

		mockMvc.perform(get("/api/users"))
		  .andExpect(status().isOk())
		  .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		  .andExpect(jsonPath("$.length()").value(3))
		  .andExpect(jsonPath("$[*].username", hasItems(username + "0", username + "1", username + "2")))
		  .andExpect(jsonPath("$[*].email", hasItems(email + "0", email + "1", email + "2")));
		;
	}

	@Test
	@DisplayName("사용자 정보 수정 Api테스트 ")
	void updateUser() throws Exception {
		// Given
		String newUsername = "newUsername";
		String newEmail = "newEamil@codeit";
		String newPassword = "newPassword";

		byte[] newBytes = "newBytes".getBytes();
		long newSize = newBytes.length;
		String newContentType = MediaType.IMAGE_PNG_VALUE;
		String newFilename = "newFilename";

		UserUpdateRequest updateRequest = UserUpdateRequest.builder()
		  .newUsername(newUsername)
		  .newEmail(newEmail)
		  .newPassword(newPassword)
		  .build();

		MockMultipartFile userPart = new MockMultipartFile(
		  "userUpdateRequest",
		  "userUpdateRequest.json",
		  MediaType.APPLICATION_JSON_VALUE,
		  objectMapper.writeValueAsBytes(updateRequest)
		);

		MockMultipartFile avatarPart = new MockMultipartFile(
		  "profile",
		  newFilename,
		  newContentType,
		  newBytes
		);

		// 기존 사용자 생성
		UserDto result = basicUserService.create(
		  CreateUserDTO.builder()
			.username(username)
			.email(email)
			.password(password)
			.binaryContent(new CreateBiContentDTO(bytes, size, contentType, filename))
			.build()

		);
		UUID userId = result.getId();

		MockMultipartHttpServletRequestBuilder builder =
		  MockMvcRequestBuilders.multipart("/api/users/");
		builder.with(request -> {
			request.setMethod("PATCH");
			return request;
		});

		mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/" + userId)
			.file(userPart)
			.file(avatarPart)
		  )
		  .andExpect(status().isOk())
		  .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		  // ApiResult 래핑
		  .andExpect(jsonPath("$.id").value(userId.toString()))
		  .andExpect(jsonPath("$.username").value(newUsername))
		  .andExpect(jsonPath("$.email").value(newEmail))
		  .andExpect(jsonPath("$.profile.fileName").value(newFilename))
		  .andExpect(jsonPath("$.profile.size").value(newSize))
		  .andExpect(jsonPath("$.profile.contentType").value(newContentType));

	}

	@Test
	@DisplayName("")
	void deleteUser() throws Exception {

		UserDto userWithProfileDto = basicUserService.create(CreateUserDTO.builder()
		  .username("userWithProfile")
		  .email("userWithProfile@email")
		  .password("userWithProfilePW")
		  .binaryContent(CreateBiContentDTO.builder()
			.content("content".getBytes())
			.size("content".getBytes().length)
			.contentType(MediaType.IMAGE_JPEG_VALUE)
			.fileName("filename")
			.build())
		  .build());

		UserDto userWithOutProfileDto = basicUserService.create(CreateUserDTO.builder()
		  .username("userWithoutProfile")
		  .email("userWithoutProfile@email")
		  .password("userWithoutProfilePW")
		  .build());

		// When & Then
		mockMvc.perform(delete("/api/users/" + userWithProfileDto.getId()))
		  .andExpect(status().isNoContent());
		mockMvc.perform(delete("/api/users/" + userWithOutProfileDto.getId()))
		  .andExpect(status().isNoContent());

		assertThat(userRepository.findById(userWithProfileDto.getId())).isEmpty();
		assertThat(userRepository.findById(userWithOutProfileDto.getId())).isEmpty();

	}
}