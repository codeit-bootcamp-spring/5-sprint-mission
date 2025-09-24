package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.storage.LocalBinaryContentStorage;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class UserApiIntegrationTest {

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private LocalBinaryContentStorage localBinaryContentStorage;

  // ===== Helpers =====
  private MockMultipartFile jsonPart(String name, Object body) throws Exception {
    return new MockMultipartFile(name, "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(body));
  }

  private MockMultipartFile jpg(String name) {
    return new MockMultipartFile(name, "p.jpg", MediaType.IMAGE_JPEG_VALUE, "img".getBytes());
  }

  // ========= Create =========
  @Nested
  class CreateUser {

    @Test
    @DisplayName("POST /api/users - 성공(201)")
    void create_success_201() throws Exception {
      var req = new UserCreateRequest("newUser", "new@user.com", "12341234");

      mockMvc.perform(
              multipart("/api/users")
                  .file(jsonPart("userCreateRequest", req))
                  .file(jpg("profile"))
                  .accept(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isCreated())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id").exists())
          .andExpect(jsonPath("$.username").value("newUser"))
          .andExpect(jsonPath("$.email").value("new@user.com"));
    }

    @Test
    @DisplayName("POST /api/users - 이메일/유저명 중복 시 409")
    @Sql("/seed.sql")
      // 기존 사용자 미리 존재
    void create_conflict_409() throws Exception {
      var req = new UserCreateRequest("user1", "user1@test.com", "12341234"); // seed와 충돌

      mockMvc.perform(
              multipart("/api/users")
                  .file(jsonPart("userCreateRequest", req))
                  .accept(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isConflict())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATE_USER.name()))
          .andExpect(jsonPath("$.status").value(ErrorCode.DUPLICATE_USER.getStatus().value()))
          .andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATE_USER.getMessage()))
          .andExpect(jsonPath("$.exceptionType").value("UserAlreadyExistsException"));
    }

    @Test
    @DisplayName("POST /api/users - 요청 유효성 검증 실패 시 400")
    void create_bad_request_400() throws Exception {
      var req = new UserCreateRequest("u1", "no_email", "short");

      mockMvc.perform(
              multipart("/api/users")
                  .file(jsonPart("userCreateRequest", req))
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name()))
          .andExpect(jsonPath("$.status").value(ErrorCode.VALIDATION_ERROR.getStatus().value()))
          .andExpect(jsonPath("$.message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
          .andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"));
    }
  }

  // ========= Update =========
  @Nested
  class UpdateUser {

    @Test
    @DisplayName("PATCH /api/users/{userId} - 성공(200)")
    @Sql("/seed.sql")
      // userId가 시드에 있어야 함
    void update_success_200() throws Exception {
      // seed.sql의 존재하는 사용자 ID
      UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
      var req = new UserUpdateRequest("updatedName", "updated@email.com", "87654321");

      mockMvc.perform(
              multipart("/api/users/{userId}", userId)
                  .file(jsonPart("userUpdateRequest", req))
                  .file(jpg("profile"))
                  .with(r -> {
                    r.setMethod(RequestMethod.PATCH.name());
                    return r;
                  }) // 멀티파트 PATCH
                  .accept(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isOk())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id").value(userId.toString()))
          .andExpect(jsonPath("$.username").value("updatedName"))
          .andExpect(jsonPath("$.email").value("updated@email.com"));
    }

    @Test
    @DisplayName("PATCH /api/users/{userId} - 존재하지 않으면 404")
    void update_notFound_404() throws Exception {
      UUID userId = UUID.randomUUID();
      var req = new UserUpdateRequest("not_found", "not_found@email.com", "12345678");

      mockMvc.perform(
              multipart("/api/users/{userId}", userId)
                  .file(jsonPart("userUpdateRequest", req))
                  .with(r -> {
                    r.setMethod("PATCH");
                    return r;
                  })
                  .accept(MediaType.APPLICATION_JSON)
          )
          .andExpect(status().isNotFound())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.name()))
          .andExpect(jsonPath("$.status").value(ErrorCode.USER_NOT_FOUND.getStatus().value()))
          .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()))
          .andExpect(jsonPath("$.exceptionType").value("UserNotFoundException"));
    }
  }

  // ========= Delete =========
  @Nested
  class DeleteUser {

    @Test
    @DisplayName("DELETE /api/users/{userId} - 성공(204)")
    @Sql("/seed.sql")
    void delete_success_204() throws Exception {
      UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");

      mockMvc.perform(delete("/api/users/{userId}", userId))
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/users/{userId} - 존재하지 않으면 404")
    void delete_notFound_404() throws Exception {
      UUID userId = UUID.randomUUID();

      mockMvc.perform(delete("/api/users/{userId}", userId).accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
  }

  // ========= FindAll =========
  @Nested
  class FindAllUsers {

    @Test
    @DisplayName("GET /api/users - 성공(200) & 리스트 반환")
    @Sql("/seed.sql")
    void findAll_success_200_list() throws Exception {
      mockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("GET /api/users - 데이터 없으면 빈 배열(200)")
    void findAll_empty_200() throws Exception {
      mockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$.length()").value(0));
    }
  }


}
