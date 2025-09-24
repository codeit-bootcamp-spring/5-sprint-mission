package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("User API 통합 테스트")
class UserIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("사용자 생성 통합 테스트 - 성공")
  void createUser_Integration_Success() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "integrationuser",
        "integration@example.com",
        "password12345"
    );

    String requestJson = objectMapper.writeValueAsString(request);
    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        requestJson.getBytes()
    );

    // when & then
    MvcResult result = mockMvc.perform(multipart("/api/users")
            .file(userPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("integrationuser"))
        .andExpect(jsonPath("$.email").value("integration@example.com"))
        .andExpect(jsonPath("$.id").exists())
        .andReturn();

    // 데이터베이스에 실제로 저장되었는지 검증
    String responseJson = result.getResponse().getContentAsString();
    String userId = objectMapper.readTree(responseJson).get("id").asText();
    
    assertThat(userRepository.findById(java.util.UUID.fromString(userId))).isPresent();
  }

  @Test
  @DisplayName("사용자 생성 통합 테스트 - 중복 이메일")
  void createUser_Integration_DuplicateEmail() throws Exception {
    // given - 첫 번째 사용자 생성
    UserCreateRequest firstRequest = new UserCreateRequest(
        "firstuser",
        "duplicate@example.com",
        "password123"
    );

    String firstJson = objectMapper.writeValueAsString(firstRequest);
    MockMultipartFile firstPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        firstJson.getBytes()
    );

    mockMvc.perform(multipart("/api/users")
        .file(firstPart)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());

    // when - 같은 이메일로 두 번째 사용자 생성 시도
    UserCreateRequest secondRequest = new UserCreateRequest(
        "seconduser",
        "duplicate@example.com",
        "password456"
    );

    String secondJson = objectMapper.writeValueAsString(secondRequest);
    MockMultipartFile secondPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        secondJson.getBytes()
    );

    // then
    mockMvc.perform(multipart("/api/users")
            .file(secondPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 수정 통합 테스트 - 성공")
  void updateUser_Integration_Success() throws Exception {
    // given - 사용자 먼저 생성
    UserCreateRequest createRequest = new UserCreateRequest(
        "originaluser",
        "original@example.com",
        "password123"
    );

    String createJson = objectMapper.writeValueAsString(createRequest);
    MockMultipartFile createPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        createJson.getBytes()
    );

    MvcResult createResult = mockMvc.perform(multipart("/api/users")
        .file(createPart)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andReturn();

    String userId = objectMapper.readTree(createResult.getResponse().getContentAsString())
        .get("id").asText();

    // when - 사용자 수정
    UserUpdateRequest updateRequest = new UserUpdateRequest(
        "updateduser",
        "updated@example.com",
        "newpassword123"
    );

    String updateJson = objectMapper.writeValueAsString(updateRequest);
    MockMultipartFile updatePart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        updateJson.getBytes()
    );

    // then
    mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(updatePart)
            .with(req -> {
              req.setMethod("PATCH");
              return req;
            })
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("updateduser"))
        .andExpect(jsonPath("$.email").value("updated@example.com"));

    // 데이터베이스에서 변경사항 검증
    var updatedUser = userRepository.findById(java.util.UUID.fromString(userId));
    assertThat(updatedUser).isPresent();
    assertThat(updatedUser.get().getUsername()).isEqualTo("updateduser");
    assertThat(updatedUser.get().getEmail()).isEqualTo("updated@example.com");
  }

  @Test
  @DisplayName("사용자 삭제 통합 테스트 - 성공")
  void deleteUser_Integration_Success() throws Exception {
    // given - 사용자 먼저 생성
    UserCreateRequest createRequest = new UserCreateRequest(
        "deleteuser",
        "delete@example.com",
        "password123"
    );

    String createJson = objectMapper.writeValueAsString(createRequest);
    MockMultipartFile createPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        createJson.getBytes()
    );

    MvcResult createResult = mockMvc.perform(multipart("/api/users")
        .file(createPart)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andReturn();

    String userId = objectMapper.readTree(createResult.getResponse().getContentAsString())
        .get("id").asText();

    // when - 사용자 삭제
    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());

    // then - 데이터베이스에서 삭제되었는지 검증
    assertThat(userRepository.findById(java.util.UUID.fromString(userId))).isEmpty();
  }

  @Test
  @DisplayName("사용자 목록 조회 통합 테스트 - 성공")
  void findAllUsers_Integration_Success() throws Exception {
    // given - 여러 사용자 생성
    String[] usernames = {"user1", "user2", "user3"};
    
    for (int i = 0; i < usernames.length; i++) {
      UserCreateRequest request = new UserCreateRequest(
          usernames[i],
          usernames[i] + "@example.com",
          "password123"
      );

      String json = objectMapper.writeValueAsString(request);
      MockMultipartFile part = new MockMultipartFile(
          "userCreateRequest",
          "",
          MediaType.APPLICATION_JSON_VALUE,
          json.getBytes()
      );

      mockMvc.perform(multipart("/api/users")
          .file(part)
          .contentType(MediaType.MULTIPART_FORM_DATA))
          .andExpect(status().isCreated());
    }

    // when & then
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].username").exists())
        .andExpect(jsonPath("$[1].username").exists())
        .andExpect(jsonPath("$[2].username").exists());
  }
}