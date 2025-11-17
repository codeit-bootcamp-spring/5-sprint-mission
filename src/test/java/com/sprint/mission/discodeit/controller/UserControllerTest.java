package com.sprint.mission.discodeit.controller;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.neutral.NewBinaryContent;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.MultipartFileMapper;
import com.sprint.mission.discodeit.service.UserService;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(GlobalExceptionHandler.class)
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private MultipartFileMapper multipartFileMapper;

	@MockitoBean
	private JpaMetamodelMappingContext mappingContext;

	// ---------------------- POST ----------------------

	@Test
	@DisplayName("create - 성공(프로필 사진 있음)")
	void createUser() throws Exception {
		UserCreateRequest userCreateRequest = new UserCreateRequest(
			"test",
			"test@email.com",
			"12341234"
		);
		MockMultipartFile userPart = new MockMultipartFile(
			"userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsBytes(userCreateRequest));

		MockMultipartFile profilePart = new MockMultipartFile(
			"profile", "profile.jpg", MediaType.IMAGE_JPEG_VALUE,
			"dummy-image".getBytes()
		);
		NewBinaryContent newBinaryContent = new NewBinaryContent(
			profilePart.getName(),
			profilePart.getContentType(),
			profilePart.getBytes()
		);
		BinaryContentDto binaryContentDto = new BinaryContentDto(
			UUID.randomUUID(),
			profilePart.getName(),
			profilePart.getSize(),
			profilePart.getContentType()
		);

		given(multipartFileMapper.toNewBinaryContent(any())).willReturn(Optional.of(newBinaryContent));

		// 응답 설계
		UserDto userDto = new UserDto(
			UUID.randomUUID(),
			"test",
			"test@email.com",
			binaryContentDto,
			false,
			Role.USER
		);

		given(userService.create(any())).willReturn(userDto);

		mockMvc.perform(
				multipart("/api/users")
					.file(userPart)
					.file(profilePart)
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id", notNullValue()))
			.andExpect(jsonPath("$.username").value("test"))
			.andExpect(jsonPath("$.email").value("test@email.com"));
	}

	@Test
	@DisplayName("create - valid 검증 실패")
	void create_invalidDto() throws Exception {
		UserCreateRequest invalid = new UserCreateRequest(" ", "not-an-email", "123");

		MockMultipartFile userPart = new MockMultipartFile(
			"userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsBytes(invalid));

		MockMultipartFile profilePart = new MockMultipartFile(
			"profile", "p.jpg", MediaType.IMAGE_JPEG_VALUE, "img".getBytes());

		mockMvc.perform(
				multipart("/api/users")
					.file(userPart)
					.file(profilePart)
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());

		then(userService).shouldHaveNoInteractions();
		then(multipartFileMapper).shouldHaveNoInteractions();
	}

	@Test
	@DisplayName("create - DTO @Valid   실패 시 ErrorResponse(details/exceptionType) 포맷 검증")
	void create_validationError_returnsStructuredError() throws Exception {
		// 가정: username @NotBlank, email @Email, password @Size(min=8)
		UserCreateRequest invalid = new UserCreateRequest(" ", "bad-email", "123");

		MockMultipartFile userPart = new MockMultipartFile(
			"userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsBytes(invalid));

		MockMultipartFile profilePart = new MockMultipartFile(
			"profile", "p.jpg", MediaType.IMAGE_JPEG_VALUE, "img".getBytes());

		mockMvc.perform(
				multipart("/api/users")
					.file(userPart)
					.file(profilePart)
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			// ErrorResponse 공통 필드
			.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.message").isString())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"))
			.andExpect(jsonPath("$.timestamp").exists())
			// ▶ details 맵에 필드별 메시지가 담겼는지 (문구는 i18n/validator 설정에 따라 달라질 수 있어 존재만 체크)
			.andExpect(jsonPath("$.details.username").isNotEmpty())
			.andExpect(jsonPath("$.details.email").isNotEmpty())
			.andExpect(jsonPath("$.details.password").isNotEmpty());

		// 실패면 서비스/매퍼는 호출되면 안 됨
		then(userService).shouldHaveNoInteractions();
		then(multipartFileMapper).shouldHaveNoInteractions();
	}

	@Test
	@DisplayName("create - 이메일 중복 시 409(CONFLICT)와 ErrorResponse 반환")
	void create_duplicateEmail_returns409() throws Exception {
		// given: 정상 요청 파트
		UserCreateRequest req = new UserCreateRequest("test", "dup@email.com", "12341234");
		MockMultipartFile userPart = new MockMultipartFile(
			"userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsBytes(req));
		MockMultipartFile profilePart = new MockMultipartFile(
			"profile", "p.jpg", MediaType.IMAGE_JPEG_VALUE, "img".getBytes());

		DiscodeitException ex = new UserAlreadyExistsException().addDetail("email", req.email());
		given(userService.create(any())).willThrow(ex);

		// when / then
		mockMvc.perform(multipart("/api/users")
				.file(userPart)
				.file(profilePart)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isConflict())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value("DUPLICATE_USER"))
			.andExpect(jsonPath("$.status").value(409))
			.andExpect(jsonPath("$.exceptionType").value(ex.getClass().getSimpleName()))
			.andExpect(jsonPath("$.message").value("이미 존재하는 사용자입니다."))
			.andExpect(jsonPath("$.details.email").value(req.email()));
	}

	// ---------------------- PATCH ----------------------

	@Test
	@DisplayName("update - 성공")
	void update() throws Exception {
		UUID userId = UUID.randomUUID();
		UserUpdateRequest req = new UserUpdateRequest("update", "update@email.com", "12341234");
		MockMultipartFile userPart = new MockMultipartFile(
			"userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsBytes(req));
		MockMultipartFile profilePart = new MockMultipartFile(
			"profile", "p.jpg", MediaType.IMAGE_JPEG_VALUE, "img".getBytes());

		NewBinaryContent newBinaryContent = new NewBinaryContent(
			profilePart.getName(),
			profilePart.getContentType(),
			profilePart.getBytes()
		);
		BinaryContentDto binaryContentDto = new BinaryContentDto(
			UUID.randomUUID(),
			profilePart.getName(),
			profilePart.getSize(),
			profilePart.getContentType()
		);

		given(multipartFileMapper.toNewBinaryContent(any())).willReturn(Optional.of(newBinaryContent));

		UserDto userDto = new UserDto(
			UUID.randomUUID(),
			"update",
			"update@email.com",
			binaryContentDto,
			false,
			Role.USER
		);

		given(userService.update(any(), any())).willReturn(userDto);

		mockMvc.perform(
				multipart("/api/users/{userId}", userId)
					.file(userPart)
					.file(profilePart)
					.with(request -> {
						request.setMethod(RequestMethod.PATCH.name());
						return request;
					})
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id", notNullValue()))
			.andExpect(jsonPath("$.username").value("update"))
			.andExpect(jsonPath("$.email").value("update@email.com"));
	}

	@Test
	@DisplayName("update - 존재하지 않는 user")
	void update_userNotFound() throws Exception {
		UUID userId = UUID.randomUUID();
		UserUpdateRequest req = new UserUpdateRequest("update", "update@email.com", "12341234");
		MockMultipartFile userPart = new MockMultipartFile(
			"userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsBytes(req));
		MockMultipartFile profilePart = new MockMultipartFile(
			"profile", "p.jpg", MediaType.IMAGE_JPEG_VALUE, "img".getBytes());

		DiscodeitException ex = new UserNotFoundException().addDetail("userId", userId);
		given(userService.update(any(), any())).willThrow(ex);

		mockMvc.perform(
				multipart("/api/users/{userId}", userId)
					.file(userPart)
					.file(profilePart)
					.with(request -> {
						request.setMethod(RequestMethod.PATCH.name());
						return request;
					})
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.exceptionType").value(ex.getClass().getSimpleName()))
			.andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."))
			.andExpect(jsonPath("$.details.userId").isNotEmpty());
	}

	// ---------------------- DELETE ----------------------

	@Test
	@DisplayName("delete - 성공 시 204(NO_CONTENT)")
	void delete_success() throws Exception {
		UUID userId = UUID.randomUUID();

		willDoNothing().given(userService).delete(userId);

		mockMvc.perform(delete("/api/users/{userId}", userId))
			.andExpect(status().isNoContent());

		then(userService).should().delete(userId);
	}

	@Test
	@DisplayName("delete - 존재하지 않는 사용자면 404 + ErrorResponse")
	void delete_notFound() throws Exception {
		UUID userId = UUID.randomUUID();

		DiscodeitException ex = new UserNotFoundException().addDetail("userId", userId);
		willThrow(ex).given(userService).delete(userId);

		mockMvc.perform(
				delete("/api/users/{userId}", userId)
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."))
			.andExpect(jsonPath("$.exceptionType").value(ex.getClass().getSimpleName()))
			.andExpect(jsonPath("$.details.userId").value(userId.toString()))
			.andExpect(jsonPath("$.timestamp").exists());

		then(userService).should().delete(userId);
	}

	// ---------------------- GET ----------------------

	@Test
	@DisplayName("findAll - 성공 시 200 + 리스트 반환")
	void findAll_success() throws Exception {
		List<UserDto> users = List.of(
			new UserDto(UUID.randomUUID(), "u1", "u1@email.com", null, false, Role.USER),
			new UserDto(UUID.randomUUID(), "u2", "u2@email.com", null, true, Role.USER)
		);
		given(userService.findAll()).willReturn(users);

		mockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].id", notNullValue()))
			.andExpect(jsonPath("$[0].username").value("u1"))
			.andExpect(jsonPath("$[0].email").value("u1@email.com"))
			.andExpect(jsonPath("$[1].username").value("u2"));
	}

	@Test
	@DisplayName("findAll - 서비스 예외 시 500 + ErrorResponse")
	void findAll_failure_internalError() throws Exception {
		given(userService.findAll()).willThrow(new RuntimeException("boom"));

		// GlobalExceptionHandler의 generic handler가 ErrorResponse(Exception, 500)을 사용한다고 가정
		mockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isInternalServerError())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value(500))
			.andExpect(jsonPath("$.code").value(
				"RuntimeException"))     // ErrorResponse(Exception, status) 규약에 맞춰서
			.andExpect(jsonPath("$.exceptionType").value("RuntimeException"))
			.andExpect(jsonPath("$.message").value("boom"))
			.andExpect(jsonPath("$.timestamp").exists());
	}
}


