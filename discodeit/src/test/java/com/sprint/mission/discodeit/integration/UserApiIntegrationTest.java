//package com.sprint.mission.discodeit.integration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sprint.mission.discodeit.dto.data.UserDto;
//import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
//import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.entity.UserStatus;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.repository.UserStatusRepository;
//import com.sprint.mission.discodeit.service.UserService;
//import com.sprint.mission.discodeit.storage.local.LocalBinaryContentStorage;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.Instant;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@Transactional
//class UserApiIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private UserStatusRepository userStatusRepository;
//
//    // 파일 IO 막기
//    @MockitoBean
//    private LocalBinaryContentStorage fileStorage;
//
//    @Test
//    @DisplayName("사용자 생성 - 멀티파트")
//    void createUser_success() throws Exception {
//        // given
//        UserCreateRequest createReq = new UserCreateRequest(
//                "user1",
//                "user1@email.com",
//                "password1"
//        );
//
//
//        MockMultipartFile userPart = new MockMultipartFile(
//                "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
//                objectMapper.writeValueAsBytes(createReq)
//        );
//
//        MockMultipartFile avatarPart = new MockMultipartFile(
//                "profile", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE,
//                "dummy-image".getBytes()
//        );
//
//        // when & then
//        mockMvc.perform(multipart("/api/users")
//                        .file(userPart)
//                        .file(avatarPart)
//                        .contentType(MediaType.MULTIPART_FORM_DATA))
//                .andExpect(status().isCreated())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                // ApiResult 래핑
//                .andExpect(jsonPath("$.username").value("user1"))
//                .andExpect(jsonPath("$.email").value("user1@email.com"));
//    }
//
//    @Test
//    @DisplayName("사용자 찾기")
//    void findUser_success() throws Exception {
//
//
//        User created = userRepository.saveAndFlush(
//                new User("testuser", "testuser@email.com", "password1", null)
//        );
//        userStatusRepository.saveAndFlush(
//                new UserStatus(created, Instant.now())
//        );
//
//
//        // when & then
//        mockMvc.perform(get("/api/users"))
//                .andExpect(status().isOk())                           // ✅ 200
//                .andExpect(jsonPath("$[0].username").value("testuser")) // ✅ 첫 번째 유저 username 확인
//                .andExpect(jsonPath("$[0].email").value("testuser@email.com"))
//                .andDo(print());
//
//    }
//}
//
