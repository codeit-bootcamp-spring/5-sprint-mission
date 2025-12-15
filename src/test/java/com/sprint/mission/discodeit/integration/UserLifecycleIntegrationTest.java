package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.global.security.ratelimit.registry.LoginRateLimitRegistry;
import com.sprint.mission.discodeit.support.IntegrationTestSupport;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import com.sprint.mission.discodeit.user.presentation.dto.UserCreateRequest;
import com.sprint.mission.discodeit.user.presentation.dto.UserUpdateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("사용자 생명주기 통합 테스트")
class UserLifecycleIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginRateLimitRegistry loginRateLimitRegistry;

    @Autowired
    private CacheManager cacheManager;

    private static final String TEST_PASSWORD = "P@ssw0rd!";
    private static final String DEFAULT_TEST_IP = "127.0.0.1";

    @BeforeEach
    void setUp() {
        loginRateLimitRegistry.resetAttempts(DEFAULT_TEST_IP);
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        loginRateLimitRegistry.resetAttempts(DEFAULT_TEST_IP);
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
    }

    private String loginAndGetAccessToken(String username) throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username)
                .param("password", TEST_PASSWORD))
            .andExpect(status().isOk())
            .andReturn();

        return objectMapper.readTree(
            loginResult.getResponse().getContentAsString()
        ).get("accessToken").asText();
    }

    @Nested
    @DisplayName("사용자 전체 생명주기")
    class FullLifecycle {

        @Test
        @DisplayName("회원가입 → 로그인 → 정보 수정 → 로그인 확인 → 삭제 전체 플로우")
        void fullUserLifecycle_success() throws Exception {
            // 1. 회원가입
            UserCreateRequest createRequest = new UserCreateRequest(
                "lifecycleuser",
                "lifecycle@example.com",
                TEST_PASSWORD
            );
            MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(createRequest)
            );

            MvcResult createResult = mockMvc.perform(multipart("/api/users")
                    .file(requestPart)
                    .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("lifecycleuser"))
                .andExpect(jsonPath("$.email").value("lifecycle@example.com"))
                .andReturn();

            String userId = objectMapper.readTree(
                createResult.getResponse().getContentAsString()
            ).get("id").asText();

            // 2. 로그인
            String accessToken = loginAndGetAccessToken("lifecycleuser");
            assertThat(accessToken).isNotEmpty();

            // 3. 정보 수정 (이메일 변경)
            UserUpdateRequest updateRequest = new UserUpdateRequest(
                null,
                "updated@example.com",
                null
            );
            MockMultipartFile updateRequestPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(updateRequest)
            );

            mockMvc.perform(multipart("/api/users/{userId}", userId)
                    .file(updateRequestPart)
                    .with(csrf())
                    .with(request -> {
                        request.setMethod("PATCH");
                        return request;
                    })
                    .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));

            // 4. 수정 후 사용자 목록에서 확인
            mockMvc.perform(get("/api/users")
                    .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("updated@example.com"));

            // 5. 사용자 삭제
            mockMvc.perform(delete("/api/users/{userId}", userId)
                    .with(csrf())
                    .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

            // 6. 삭제 확인
            assertThat(userRepository.findById(UUID.fromString(userId))).isEmpty();
        }
    }

    @Nested
    @DisplayName("회원가입")
    class Registration {

        @Test
        @DisplayName("유효한 정보로 회원가입 성공")
        void register_withValidData_success() throws Exception {
            UserCreateRequest request = new UserCreateRequest(
                "newuser",
                "newuser@example.com",
                TEST_PASSWORD
            );
            MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            mockMvc.perform(multipart("/api/users")
                    .file(requestPart)
                    .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.id").exists());
        }

        @Test
        @DisplayName("프로필 이미지와 함께 회원가입 성공")
        void register_withProfileImage_success() throws Exception {
            UserCreateRequest request = new UserCreateRequest(
                "userwithprofile",
                "withprofile@example.com",
                TEST_PASSWORD
            );
            MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );
            MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "profile.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake image content".getBytes()
            );

            mockMvc.perform(multipart("/api/users")
                    .file(requestPart)
                    .file(profile)
                    .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("userwithprofile"))
                .andExpect(jsonPath("$.profile").exists());
        }

        @Test
        @DisplayName("중복된 username으로 회원가입 시 409 반환")
        void register_withDuplicateUsername_returns409() throws Exception {
            // 첫 번째 사용자 생성
            UserCreateRequest firstRequest = new UserCreateRequest(
                "duplicateuser",
                "first@example.com",
                TEST_PASSWORD
            );
            MockMultipartFile firstRequestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(firstRequest)
            );
            mockMvc.perform(multipart("/api/users")
                    .file(firstRequestPart)
                    .with(csrf()))
                .andExpect(status().isCreated());

            // 같은 username으로 두 번째 사용자 생성 시도
            UserCreateRequest duplicateRequest = new UserCreateRequest(
                "duplicateuser",
                "second@example.com",
                TEST_PASSWORD
            );
            MockMultipartFile duplicateRequestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(duplicateRequest)
            );

            mockMvc.perform(multipart("/api/users")
                    .file(duplicateRequestPart)
                    .with(csrf()))
                .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("중복된 email로 회원가입 시 409 반환")
        void register_withDuplicateEmail_returns409() throws Exception {
            // 첫 번째 사용자 생성
            UserCreateRequest firstRequest = new UserCreateRequest(
                "user1",
                "duplicate@example.com",
                TEST_PASSWORD
            );
            MockMultipartFile firstRequestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(firstRequest)
            );
            mockMvc.perform(multipart("/api/users")
                    .file(firstRequestPart)
                    .with(csrf()))
                .andExpect(status().isCreated());

            // 같은 email로 두 번째 사용자 생성 시도
            UserCreateRequest duplicateRequest = new UserCreateRequest(
                "user2",
                "duplicate@example.com",
                TEST_PASSWORD
            );
            MockMultipartFile duplicateRequestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(duplicateRequest)
            );

            mockMvc.perform(multipart("/api/users")
                    .file(duplicateRequestPart)
                    .with(csrf()))
                .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("필수 필드 누락 시 400 반환")
        void register_withMissingFields_returns400() throws Exception {
            UserCreateRequest request = new UserCreateRequest(
                "",
                "test@example.com",
                TEST_PASSWORD
            );
            MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            mockMvc.perform(multipart("/api/users")
                    .file(requestPart)
                    .with(csrf()))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("사용자 조회")
    class FindUsers {

        @Test
        @WithMockUser
        @DisplayName("전체 사용자 목록 조회 성공")
        void findAll_success() throws Exception {
            // Given: 2명의 사용자 생성
            for (int i = 1; i <= 2; i++) {
                UserCreateRequest request = new UserCreateRequest(
                    "user" + i,
                    "user" + i + "@example.com",
                    TEST_PASSWORD
                );
                MockMultipartFile requestPart = new MockMultipartFile(
                    "userCreateRequest",
                    "",
                    MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(request)
                );
                mockMvc.perform(multipart("/api/users")
                        .file(requestPart)
                        .with(csrf()))
                    .andExpect(status().isCreated());
            }

            // When & Then
            mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("인증 없이 사용자 목록 조회 시 403 반환")
        void findAll_withoutAuth_returns403() throws Exception {
            mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("사용자 정보 수정")
    class UpdateUser {

        @Test
        @DisplayName("본인 정보 수정 성공")
        void update_ownInfo_success() throws Exception {
            // Given: 사용자 생성 및 로그인
            UserCreateRequest createRequest = new UserCreateRequest(
                "updateuser",
                "update@example.com",
                TEST_PASSWORD
            );
            MockMultipartFile createRequestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(createRequest)
            );
            MvcResult createResult = mockMvc.perform(multipart("/api/users")
                    .file(createRequestPart)
                    .with(csrf()))
                .andExpect(status().isCreated())
                .andReturn();

            String userId = objectMapper.readTree(
                createResult.getResponse().getContentAsString()
            ).get("id").asText();

            String accessToken = loginAndGetAccessToken("updateuser");

            // When & Then: 이메일 변경
            UserUpdateRequest updateRequest = new UserUpdateRequest(
                null,
                "newemail@example.com",
                null
            );
            MockMultipartFile updateRequestPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(updateRequest)
            );

            mockMvc.perform(multipart("/api/users/{userId}", userId)
                    .file(updateRequestPart)
                    .with(csrf())
                    .with(request -> {
                        request.setMethod("PATCH");
                        return request;
                    })
                    .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newemail@example.com"));
        }

        @Test
        @DisplayName("다른 사용자 정보 수정 시 403 반환")
        void update_otherUserInfo_returns403() throws Exception {
            // Given: 두 명의 사용자 생성
            UserCreateRequest user1Request = new UserCreateRequest(
                "user1",
                "user1@example.com",
                TEST_PASSWORD
            );
            MockMultipartFile user1RequestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(user1Request)
            );
            MvcResult user1Result = mockMvc.perform(multipart("/api/users")
                    .file(user1RequestPart)
                    .with(csrf()))
                .andExpect(status().isCreated())
                .andReturn();

            String user1Id = objectMapper.readTree(
                user1Result.getResponse().getContentAsString()
            ).get("id").asText();

            UserCreateRequest user2Request = new UserCreateRequest(
                "user2",
                "user2@example.com",
                TEST_PASSWORD
            );
            MockMultipartFile user2RequestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(user2Request)
            );
            mockMvc.perform(multipart("/api/users")
                    .file(user2RequestPart)
                    .with(csrf()))
                .andExpect(status().isCreated());

            // user2로 로그인
            String user2Token = loginAndGetAccessToken("user2");

            // When & Then: user2가 user1 정보 수정 시도
            UserUpdateRequest updateRequest = new UserUpdateRequest(
                null,
                "hacked@example.com",
                null
            );
            MockMultipartFile updateRequestPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(updateRequest)
            );

            mockMvc.perform(multipart("/api/users/{userId}", user1Id)
                    .file(updateRequestPart)
                    .with(csrf())
                    .with(request -> {
                        request.setMethod("PATCH");
                        return request;
                    })
                    .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("사용자 삭제")
    class DeleteUser {

        @Test
        @DisplayName("본인 계정 삭제 성공")
        void delete_ownAccount_success() throws Exception {
            // Given: 사용자 생성 및 로그인
            UserCreateRequest createRequest = new UserCreateRequest(
                "deleteuser",
                "delete@example.com",
                TEST_PASSWORD
            );
            MockMultipartFile createRequestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(createRequest)
            );
            MvcResult createResult = mockMvc.perform(multipart("/api/users")
                    .file(createRequestPart)
                    .with(csrf()))
                .andExpect(status().isCreated())
                .andReturn();

            String userId = objectMapper.readTree(
                createResult.getResponse().getContentAsString()
            ).get("id").asText();

            String accessToken = loginAndGetAccessToken("deleteuser");

            // When & Then
            mockMvc.perform(delete("/api/users/{userId}", userId)
                    .with(csrf())
                    .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

            // 삭제 확인
            assertThat(userRepository.findById(UUID.fromString(userId))).isEmpty();
        }

        @Test
        @DisplayName("다른 사용자 계정 삭제 시 403 반환")
        void delete_otherAccount_returns403() throws Exception {
            // Given: 두 명의 사용자 생성
            UserCreateRequest targetRequest = new UserCreateRequest(
                "targetuser",
                "target@example.com",
                TEST_PASSWORD
            );
            MockMultipartFile targetRequestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(targetRequest)
            );
            MvcResult targetResult = mockMvc.perform(multipart("/api/users")
                    .file(targetRequestPart)
                    .with(csrf()))
                .andExpect(status().isCreated())
                .andReturn();

            String targetUserId = objectMapper.readTree(
                targetResult.getResponse().getContentAsString()
            ).get("id").asText();

            UserCreateRequest attackerRequest = new UserCreateRequest(
                "attacker",
                "attacker@example.com",
                TEST_PASSWORD
            );
            MockMultipartFile attackerRequestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(attackerRequest)
            );
            mockMvc.perform(multipart("/api/users")
                    .file(attackerRequestPart)
                    .with(csrf()))
                .andExpect(status().isCreated());

            String attackerToken = loginAndGetAccessToken("attacker");

            // When & Then
            mockMvc.perform(delete("/api/users/{userId}", targetUserId)
                    .with(csrf())
                    .header("Authorization", "Bearer " + attackerToken))
                .andExpect(status().isForbidden());
        }
    }
}
