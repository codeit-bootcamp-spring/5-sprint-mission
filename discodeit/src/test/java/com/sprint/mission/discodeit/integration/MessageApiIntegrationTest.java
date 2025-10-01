//package com.sprint.mission.discodeit.integration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sprint.mission.discodeit.dto.data.MessageDto;
//import com.sprint.mission.discodeit.dto.data.UserDto;
//import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
//import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
//import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
//import com.sprint.mission.discodeit.entity.*;
//import com.sprint.mission.discodeit.repository.ChannelRepository;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.repository.UserStatusRepository;
//import com.sprint.mission.discodeit.service.MessageService;
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
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@Transactional
//class MessageApiIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private MessageService messageService;
//
//    @Autowired
//    private MessageRepository messageRepository;
//    @Autowired
//    private ChannelRepository channelRepository;
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
//    @DisplayName("메시지 생성")
//    void createMessage_success() throws Exception {
//        // given
//        Channel channel = new Channel(ChannelType.PUBLIC,"bus","bustam");
//        channelRepository.saveAndFlush(channel);
//
//        User created = userRepository.saveAndFlush(
//                new User("testuser", "testuser@email.com", "password1", null)
//        );
//        userStatusRepository.saveAndFlush(
//                new UserStatus(created, Instant.now())
//        );
//
//
//
//        MessageCreateRequest createReq = new MessageCreateRequest(
//                "plz",
//                channel.getId(),
//                created.getId()
//        );
//
//
//        MockMultipartFile messagePart = new MockMultipartFile(
//                "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
//                objectMapper.writeValueAsBytes(createReq)
//        );
//
//        MockMultipartFile filePart = new MockMultipartFile(
//                "attachments", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE,
//                "dummy-image".getBytes()
//        );
//
//        // when & then
//        mockMvc.perform(multipart("/api/messages")
//                        .file(messagePart)
//                        .file(filePart)
//                        .contentType(MediaType.MULTIPART_FORM_DATA))
//                .andExpect(status().isCreated())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                // ApiResult 래핑
//                .andExpect(jsonPath("$.content").value("plz"));
//    }
//
//    @Test
//    @DisplayName("메시지 삭제")
//    void deleteMessage_success() throws Exception {
//        List<BinaryContent> profileList = Collections.emptyList();
//        Channel channel = new Channel(ChannelType.PUBLIC,"bus","bustam");
//        channelRepository.saveAndFlush(channel);
//
//        User user = userRepository.saveAndFlush(
//                new User("testuser", "testuser@email.com", "password1", null)
//        );
//        userStatusRepository.saveAndFlush(
//                new UserStatus(user, Instant.now())
//        );
//
//        Message message = new Message("plz",channel,user,profileList);
//        messageRepository.save(message);
//
//        // when & then
//        mockMvc.perform(delete("/api/messages/{id}", message.getId()))
//                .andExpect(status().isNoContent())   // ✅ 204 응답 검사
//                .andExpect(content().string(""));    // ✅ body 비어 있는지 확인
//    }
//}
//
