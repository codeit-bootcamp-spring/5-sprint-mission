package com.sprint.mission.discodeit.controller;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMethod;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private UserService userService;
  @MockitoBean private UserStatusService userStatusService;
  @MockitoBean private JpaMetamodelMappingContext mappingContext;

  // ---------------------- POST ----------------------

  @Test
  @DisplayName("create - 성공(프로필 사진 있음)")
  void createUser() throws Exception {
    UserCreateRequest userCreateRequest =
        new UserCreateRequest("test", "test@email.com", "12341234");
    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(userCreateRequest));

    MockMultipartFile profilePart =
        new MockMultipartFile("profile", "profile.jpg", MediaType.IMAGE_JPEG_VALUE, "dummy".getBytes());

    BinaryContentDto profileDto = new BinaryContentDto(
        UUID.randomUUID(), profilePart.getName(), profilePart.getSize(), profilePart.getContentType());

    UserDto userDto = new UserDto(
        UUID.randomUUID(), "test", "test@email.com", profileDto, false);

    // 메인: create(req, file/option) 시그니처 2개
    given(userService.create(any(), any())).willReturn(userDto);

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
    MockMultipartFile profilePart =
        new MockMultipartFile("profile", "p.jpg", MediaType.IMAGE_JPEG_VALUE, "img".getBytes());

    mockMvc.perform(
            multipart("/api/users")
                .file(userPart)
                .file(profilePart)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    then(userService).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("create - DTO @Valid 실패 시 ErrorResponse 기본 필드 확인")
  void create_validationError_returnsStructuredError() throws Exception {
    UserCreateRequest invalid = new UserCreateRequest(" ", "bad-email", "123");

    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(invalid));
    MockMultipartFile profilePart =
        new MockMultipartFile("profile", "p.jpg", MediaType.IMAGE_JPEG_VALUE, "img".getBytes());

    mockMvc.perform(
            multipart("/api/users")
                .file(userPart)
                .file(profilePart)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").isString())
        .andExpect(jsonPath("$.message").isString())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.exceptionType").isString())
        .andExpect(jsonPath("$.timestamp").exists());

    then(userService).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("create - 이메일 중복 시 409(CONFLICT)와 ErrorResponse 반환")
  void create_duplicateEmail_returns409() throws Exception {
    UserCreateRequest req = new UserCreateRequest("test", "dup@email.com", "12341234");
    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(req));
    MockMultipartFile profilePart =
        new MockMultipartFile("profile", "p.jpg", MediaType.IMAGE_JPEG_VALUE, "img".getBytes());

    DiscodeitException ex = new UserAlreadyExistsException("email", req.email());
    given(userService.create(any(), any())).willThrow(ex);

    mockMvc.perform(multipart("/api/users")
            .file(userPart)
            .file(profilePart)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value(ex.getErrorCode().name()))
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.exceptionType").value(ex.getClass().getSimpleName()))
        .andExpect(jsonPath("$.message").value(ex.getErrorCode().getMessage()));
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
    MockMultipartFile profilePart =
        new MockMultipartFile("profile", "p.jpg", MediaType.IMAGE_JPEG_VALUE, "img".getBytes());

    BinaryContentDto profileDto = new BinaryContentDto(
        UUID.randomUUID(), profilePart.getName(), profilePart.getSize(), profilePart.getContentType());

    UserDto userDto = new UserDto(
        UUID.randomUUID(), "update", "update@email.com", profileDto, false);

    // 메인: update(userId, req, file/option) 시그니처 3개
    given(userService.update(any(), any(), any())).willReturn(userDto);

    mockMvc.perform(
            multipart("/api/users/{userId}", userId)
                .file(userPart)
                .file(profilePart)
                .with(reqWrapper -> { reqWrapper.setMethod(RequestMethod.PATCH.name()); return reqWrapper; })
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.username").value("update"))
        .andExpect(jsonPath("$.email").value("update@email.com"));
  }

  @Test
  @DisplayName("update - 존재하지 않는 user → 404")
  void update_userNotFound() throws Exception {
    UUID userId = UUID.randomUUID();
    UserUpdateRequest req = new UserUpdateRequest("update", "update@email.com", "12341234");
    MockMultipartFile userPart = new MockMultipartFile(
        "userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(req));
    MockMultipartFile profilePart =
        new MockMultipartFile("profile", "p.jpg", MediaType.IMAGE_JPEG_VALUE, "img".getBytes());

    DiscodeitException ex = new UserNotFoundException(userId);
    given(userService.update(any(), any(), any())).willThrow(ex);

    mockMvc.perform(
            multipart("/api/users/{userId}", userId)
                .file(userPart)
                .file(profilePart)
                .with(reqWrapper -> { reqWrapper.setMethod(RequestMethod.PATCH.name()); return reqWrapper; })
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value(ex.getErrorCode().name()))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.exceptionType").value(ex.getClass().getSimpleName()))
        .andExpect(jsonPath("$.message").value(ex.getErrorCode().getMessage()));
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

    DiscodeitException ex = new UserNotFoundException(userId);
    willThrow(ex).given(userService).delete(userId);

    mockMvc.perform(
            delete("/api/users/{userId}", userId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value(ex.getErrorCode().name()))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value(ex.getErrorCode().getMessage()))
        .andExpect(jsonPath("$.exceptionType").value(ex.getClass().getSimpleName()))
        .andExpect(jsonPath("$.timestamp").exists());

    then(userService).should().delete(userId);
  }

  // ---------------------- GET ----------------------

  @Test
  @DisplayName("findAll - 성공 시 200 + 리스트 반환")
  void findAll_success() throws Exception {
    List<UserDto> users = List.of(
        new UserDto(UUID.randomUUID(), "u1", "u1@email.com", null, false),
        new UserDto(UUID.randomUUID(), "u2", "u2@email.com", null, true)
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

    mockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.code").isString())
        .andExpect(jsonPath("$.exceptionType").value("RuntimeException"))
        .andExpect(jsonPath("$.message").value("boom"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  // ---------------------- PATCH(userStatus) ----------------------

  @Test
  @DisplayName("updateUserStatus - 성공")
  void updateUserStatus() throws Exception {
    UUID userId = UUID.randomUUID();
    Instant lastActiveAt = Instant.now();
    UserStatusUpdateRequest req = new UserStatusUpdateRequest(lastActiveAt);
    String requestBody = objectMapper.writeValueAsString(req);
    UserStatusDto userStatusDto = new UserStatusDto(UUID.randomUUID(), userId, lastActiveAt);

    given(userStatusService.updateByUserId(any(), any())).willReturn(userStatusDto);

    mockMvc.perform(
            patch("/api/users/{userId}/userStatus", userId)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.lastActiveAt").isNotEmpty());
  }

  @Test
  @DisplayName("updateUserStatus - 실패(userStatus를 찾을 수 없음)")
  void updateUserStatus_userStatusNotFound() throws Exception {
    UUID userId = UUID.randomUUID();
    Instant lastActiveAt = Instant.now();
    UserStatusUpdateRequest req = new UserStatusUpdateRequest(lastActiveAt);
    String requestBody = objectMapper.writeValueAsString(req);

    DiscodeitException ex = new UserStatusNotFoundException(userId);
    given(userStatusService.updateByUserId(any(), any())).willThrow(ex);

    mockMvc.perform(
            patch("/api/users/{userId}/userStatus", userId)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value(ex.getErrorCode().name()))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.exceptionType").value(ex.getClass().getSimpleName()))
        .andExpect(jsonPath("$.message").value(ex.getErrorCode().getMessage()));
  }
}
