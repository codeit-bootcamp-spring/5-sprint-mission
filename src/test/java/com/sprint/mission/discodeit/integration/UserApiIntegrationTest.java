package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.TestSecurityConfig;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.WithMockDiscodeitUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(TestSecurityConfig.class)
class UserApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    private void setSecurityContextForUser(User user) {
        UserDto userDto = new UserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            null,
            true,
            user.getRole()
        );
        DiscodeitUserDetails principal = new DiscodeitUserDetails(userDto, "password");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            principal, principal.getPassword(), principal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("사용자 생성 - 성공: 프로필 없이 사용자를 생성하고 데이터베이스에 저장됨")
    void createUser_WithoutProfile_Success() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest(
            "integrationuser",
            "integration@example.com",
            "password123"
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        // when
        String responseBody = mockMvc.perform(multipart("/api/users")
                .file(requestPart))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("integrationuser"))
            .andExpect(jsonPath("$.email").value("integration@example.com"))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.profile").doesNotExist())
            .andReturn()
            .getResponse()
            .getContentAsString();

        // then - 데이터베이스에 실제로 저장되었는지 확인
        String userId = objectMapper.readTree(responseBody).get("id").asText();
        Optional<User> savedUser = userRepository.findById(UUID.fromString(userId));

        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getUsername()).isEqualTo("integrationuser");
        assertThat(savedUser.get().getEmail()).isEqualTo("integration@example.com");
        assertThat(savedUser.get().getProfile()).isNull();
    }

    @Test
    @DisplayName("사용자 생성 - 성공: 프로필 이미지와 함께 사용자를 생성하고 파일이 저장됨")
    void createUser_WithProfile_Success() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest(
            "profileuser",
            "profile@example.com",
            "password123"
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile profilePart = new MockMultipartFile(
            "profile",
            "profile.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "test image content".getBytes()
        );

        // when
        String responseBody = mockMvc.perform(multipart("/api/users")
                .file(requestPart)
                .file(profilePart))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("profileuser"))
            .andExpect(jsonPath("$.profile").exists())
            .andExpect(jsonPath("$.profile.fileName").value("profile.jpg"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        // then - 데이터베이스 검증
        String userId = objectMapper.readTree(responseBody).get("id").asText();
        Optional<User> savedUser = userRepository.findById(UUID.fromString(userId));

        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getProfile()).isNotNull();
        assertThat(savedUser.get().getProfile().getFileName()).isEqualTo("profile.jpg");

        // BinaryContent도 저장되었는지 확인
        assertThat(binaryContentRepository.findById(savedUser.get().getProfile().getId())).isPresent();
    }

    @Test
    @DisplayName("사용자 생성 - 실패: 유효하지 않은 데이터로 생성 시도")
    void createUser_InvalidData_Fails() throws Exception {
        // given - 빈 username
        UserCreateRequest request = new UserCreateRequest(
            "",
            "test@example.com",
            "password123"
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        // when & then
        mockMvc.perform(multipart("/api/users")
                .file(requestPart))
            .andExpect(status().isBadRequest());

        // 데이터베이스에 저장되지 않았는지 확인
        assertThat(userRepository.findByUsername("")).isEmpty();
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("사용자 목록 조회 - 성공: 모든 사용자를 조회하고 프로필 정보가 포함됨")
    void findAllUsers_Success() throws Exception {
        // given - 사용자 2명 생성
        User user1 = new User("listuser1", "list1@example.com", "encoded1", null);
        User user2 = new User("listuser2", "list2@example.com", "encoded2", null);
        userRepository.saveAll(java.util.List.of(user1, user2));

        // when & then
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].username").exists())
            .andExpect(jsonPath("$[1].username").exists());
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("사용자 목록 조회 - 성공: 사용자가 없으면 빈 배열 반환")
    void findAllUsers_EmptyList() throws Exception {
        // when & then
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("사용자 수정 - 성공: 사용자 정보를 수정하고 데이터베이스에 반영됨")
    void updateUser_Success() throws Exception {
        // given - 사용자 생성
        User user = new User("updateuser", "update@example.com", "encoded", null);
        userRepository.save(user);

        // 생성된 사용자로 보안 컨텍스트 설정
        setSecurityContextForUser(user);

        UserUpdateRequest request = new UserUpdateRequest(
            "newusername",
            "newemail@example.com",
            "newpassword"
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "userUpdateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        // when
        mockMvc.perform(multipart("/api/users/{userId}", user.getId())
                .file(requestPart)
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                }))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("newusername"))
            .andExpect(jsonPath("$.email").value("newemail@example.com"));

        // then - 데이터베이스에 실제로 수정되었는지 확인
        Optional<User> updatedUser = userRepository.findById(user.getId());
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getUsername()).isEqualTo("newusername");
        assertThat(updatedUser.get().getEmail()).isEqualTo("newemail@example.com");
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("사용자 수정 - 실패: 존재하지 않는 사용자 수정 시도")
    void updateUser_NotFound_Fails() throws Exception {
        // given
        UUID nonExistentId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest(
            "newusername",
            "newemail@example.com",
            null
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "userUpdateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        // when & then
        mockMvc.perform(multipart("/api/users/{userId}", nonExistentId)
                .file(requestPart)
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                }))
            .andExpect(status().isForbidden());  // 권한 검사에서 실패 (자신의 정보가 아님)
    }

    @Test
    @DisplayName("사용자 삭제 - 성공: 사용자를 삭제하고 데이터베이스에서 제거됨")
    void deleteUser_Success() throws Exception {
        // given - 사용자 생성
        User user = new User("deleteuser", "delete@example.com", "encoded", null);
        userRepository.save(user);
        UUID userId = user.getId();

        // 생성된 사용자로 보안 컨텍스트 설정
        setSecurityContextForUser(user);

        // when
        mockMvc.perform(delete("/api/users/{userId}", userId))
            .andExpect(status().isNoContent());

        // then - 데이터베이스에서 실제로 삭제되었는지 확인
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("사용자 삭제 - 실패: 존재하지 않는 사용자 삭제 시도")
    void deleteUser_NotFound_Fails() throws Exception {
        // given
        UUID nonExistentId = UUID.randomUUID();

        // when & then
        mockMvc.perform(delete("/api/users/{userId}", nonExistentId))
            .andExpect(status().isForbidden());  // 권한 검사에서 실패 (자신의 정보가 아님)
    }

    @Test
    @DisplayName("사용자 생성 - 실패: 중복된 사용자명으로 생성 시도")
    void createUser_DuplicateUsername_Fails() throws Exception {
        // given - 먼저 사용자 생성
        User existingUser = new User("duplicateuser", "unique@example.com", "encoded", null);
        userRepository.saveAndFlush(existingUser);

        // 같은 username으로 새 사용자 생성 시도
        UserCreateRequest request = new UserCreateRequest(
            "duplicateuser",
            "different@example.com",
            "password123"
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        // when & then
        mockMvc.perform(multipart("/api/users")
                .file(requestPart))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATE_USERNAME"))
            .andExpect(jsonPath("$.message").value("중복된 사용자명입니다."))
            .andExpect(jsonPath("$.details.username").value("duplicateuser"));
    }

    @Test
    @DisplayName("사용자 생성 - 실패: 중복된 이메일로 생성 시도")
    void createUser_DuplicateEmail_Fails() throws Exception {
        // given - 먼저 사용자 생성
        User existingUser = new User("uniqueuser", "duplicate@example.com", "encoded", null);
        userRepository.saveAndFlush(existingUser);

        // 같은 email로 새 사용자 생성 시도
        UserCreateRequest request = new UserCreateRequest(
            "differentuser",
            "duplicate@example.com",
            "password123"
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        // when & then
        mockMvc.perform(multipart("/api/users")
                .file(requestPart))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"))
            .andExpect(jsonPath("$.message").value("중복된 이메일입니다."))
            .andExpect(jsonPath("$.details.email").value("duplicate@example.com"));
    }

    @Test
    @DisplayName("사용자 수정 - 실패: 중복된 사용자명으로 수정 시도")
    void updateUser_DuplicateUsername_Fails() throws Exception {
        // given - 두 명의 사용자 생성
        User user1 = new User("user1", "user1@example.com", "encoded", null);
        User user2 = new User("user2", "user2@example.com", "encoded", null);
        userRepository.saveAllAndFlush(java.util.List.of(user1, user2));

        // user2로 보안 컨텍스트 설정
        setSecurityContextForUser(user2);

        // user2의 username을 user1의 username으로 변경 시도
        UserUpdateRequest request = new UserUpdateRequest(
            "user1",
            null,
            null
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "userUpdateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        // when & then
        mockMvc.perform(multipart("/api/users/{userId}", user2.getId())
                .file(requestPart)
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                }))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATE_USERNAME"))
            .andExpect(jsonPath("$.message").value("중복된 사용자명입니다."));
    }

    @Test
    @DisplayName("사용자 수정 - 실패: 중복된 이메일로 수정 시도")
    void updateUser_DuplicateEmail_Fails() throws Exception {
        // given - 두 명의 사용자 생성
        User user1 = new User("user1", "user1@example.com", "encoded", null);
        User user2 = new User("user2", "user2@example.com", "encoded", null);
        userRepository.saveAllAndFlush(java.util.List.of(user1, user2));

        // user2로 보안 컨텍스트 설정
        setSecurityContextForUser(user2);

        // user2의 email을 user1의 email로 변경 시도
        UserUpdateRequest request = new UserUpdateRequest(
            null,
            "user1@example.com",
            null
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "userUpdateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        // when & then
        mockMvc.perform(multipart("/api/users/{userId}", user2.getId())
                .file(requestPart)
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                }))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"))
            .andExpect(jsonPath("$.message").value("중복된 이메일입니다."));
    }
}
