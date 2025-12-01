package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageAttachment;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageAttachmentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.userdetails.WithMockDiscodeitUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.support.TestFixtures.createFilePart;
import static com.sprint.mission.discodeit.support.TestFixtures.createJsonRequestPart;
import static com.sprint.mission.discodeit.support.TestFixtures.createPublicChannel;
import static com.sprint.mission.discodeit.support.TestFixtures.createUser;
import static com.sprint.mission.discodeit.support.TestFixtures.setSecurityContextForUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MessageApiIntegrationTest extends CacheClearTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private MessageAttachmentRepository messageAttachmentRepository;

    @Test
    @WithMockDiscodeitUser
    @DisplayName("메시지 생성 - 성공: 첨부파일 없이 메시지를 생성하고 데이터베이스에 저장됨")
    void createMessage_WithoutAttachments_Success() throws Exception {
        // given - 사용자와 채널 생성
        User author = createUser("author");
        userRepository.save(author);

        Channel channel = createPublicChannel("TestChannel");
        channelRepository.save(channel);

        MessageCreateRequest request = new MessageCreateRequest(
            "Hello, World!",
            channel.getId(),
            author.getId()
        );

        MockMultipartFile requestPart = createJsonRequestPart("messageCreateRequest", request, objectMapper);

        // when
        String responseBody = mockMvc.perform(multipart("/api/messages")
                .file(requestPart)
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("Hello, World!"))
            .andExpect(jsonPath("$.author.id").value(author.getId().toString()))
            .andExpect(jsonPath("$.channelId").value(channel.getId().toString()))
            .andReturn()
            .getResponse()
            .getContentAsString();

        // then - 데이터베이스에 실제로 저장되었는지 확인
        String messageId = objectMapper.readTree(responseBody).get("id").asText();
        Optional<Message> savedMessage = messageRepository.findById(UUID.fromString(messageId));

        assertThat(savedMessage).isPresent();
        assertThat(savedMessage.get().getContent()).isEqualTo("Hello, World!");
        assertThat(savedMessage.get().getAuthor().getId()).isEqualTo(author.getId());
        assertThat(savedMessage.get().getChannel().getId()).isEqualTo(channel.getId());
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("메시지 생성 - 성공: 첨부파일과 함께 메시지를 생성하고 MessageAttachment가 생성됨")
    void createMessage_WithAttachments_Success() throws Exception {
        // given - 사용자와 채널 생성
        User author = createUser("author");
        userRepository.save(author);

        Channel channel = createPublicChannel("TestChannel");
        channelRepository.save(channel);

        MessageCreateRequest request = new MessageCreateRequest(
            "Message with files",
            channel.getId(),
            author.getId()
        );

        MockMultipartFile requestPart = createJsonRequestPart("messageCreateRequest", request, objectMapper);
        MockMultipartFile attachment = createFilePart(
            "attachments", "test.txt", MediaType.TEXT_PLAIN_VALUE, "test content".getBytes()
        );

        // when
        String responseBody = mockMvc.perform(multipart("/api/messages")
                .file(requestPart)
                .file(attachment)
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("Message with files"))
            .andExpect(jsonPath("$.attachments").isArray())
            .andExpect(jsonPath("$.attachments[0].fileName").value("test.txt"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        // then - 데이터베이스 검증
        String messageId = objectMapper.readTree(responseBody).get("id").asText();
        Optional<Message> savedMessage = messageRepository.findById(UUID.fromString(messageId));

        assertThat(savedMessage).isPresent();

        // MessageAttachment 확인
        List<BinaryContent> attachments =
            messageAttachmentRepository.findAttachmentsByMessageId(savedMessage.get().getId());
        assertThat(attachments).hasSize(1);
        assertThat(attachments.get(0).getFileName()).isEqualTo("test.txt");

        // BinaryContent도 저장되었는지 확인
        assertThat(binaryContentRepository.findById(attachments.get(0).getId())).isPresent();
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("메시지 생성 - 실패: 유효하지 않은 데이터로 생성 시도")
    void createMessage_InvalidData_Fails() throws Exception {
        // given - channelId가 null (NotNull 제약 위반)
        User author = createUser("author");
        userRepository.save(author);

        MessageCreateRequest request = new MessageCreateRequest(
            "Hello",
            null,
            author.getId()
        );

        MockMultipartFile requestPart = createJsonRequestPart("messageCreateRequest", request, objectMapper);

        // when & then
        mockMvc.perform(multipart("/api/messages")
                .file(requestPart)
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("메시지 목록 조회 - 성공: 채널의 메시지를 페이지네이션으로 조회")
    void findMessages_Success() throws Exception {
        // given - 사용자, 채널, 메시지 생성
        User author = createUser("author");
        userRepository.save(author);

        Channel channel = createPublicChannel("TestChannel");
        channelRepository.save(channel);

        Message message1 = new Message("Message 1", channel, author);
        Message message2 = new Message("Message 2", channel, author);
        messageRepository.saveAll(List.of(message1, message2));

        // when & then
        mockMvc.perform(get("/api/messages")
                .param("channelId", channel.getId().toString())
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].content").exists())
            .andExpect(jsonPath("$.content[1].content").exists());
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("메시지 목록 조회 - 성공: 메시지가 없으면 빈 배열 반환")
    void findMessages_EmptyList() throws Exception {
        // given - 채널만 생성
        Channel channel = createPublicChannel("EmptyChannel");
        channelRepository.save(channel);

        // when & then
        mockMvc.perform(get("/api/messages")
                .param("channelId", channel.getId().toString())
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    @DisplayName("메시지 수정 - 성공: 메시지 내용을 수정하고 데이터베이스에 반영됨")
    void updateMessage_Success() throws Exception {
        // given - 메시지 생성
        User author = createUser("author");
        userRepository.save(author);

        Channel channel = createPublicChannel("TestChannel");
        channelRepository.save(channel);

        Message message = new Message("Original content", channel, author);
        messageRepository.save(message);

        // 작성자로 보안 컨텍스트 설정
        setSecurityContextForUser(author);

        MessageUpdateRequest request = new MessageUpdateRequest("Updated content");

        // when
        mockMvc.perform(patch("/api/messages/{messageId}", message.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value("Updated content"));

        // then - 데이터베이스에 실제로 수정되었는지 확인
        Optional<Message> updatedMessage = messageRepository.findById(message.getId());
        assertThat(updatedMessage).isPresent();
        assertThat(updatedMessage.get().getContent()).isEqualTo("Updated content");
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("메시지 수정 - 실패: 존재하지 않는 메시지 수정 시도")
    void updateMessage_NotFound_Fails() throws Exception {
        // given
        UUID nonExistentId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("Updated content");

        // when & then - 작성자가 아니므로 AccessDenied (403)
        mockMvc.perform(patch("/api/messages/{messageId}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("메시지 삭제 - 성공: 메시지와 첨부파일이 모두 삭제됨")
    void deleteMessage_Success() throws Exception {
        // given - 메시지와 첨부파일 생성
        User author = createUser("author");
        userRepository.save(author);

        Channel channel = createPublicChannel("TestChannel");
        channelRepository.save(channel);

        Message message = new Message("To be deleted", channel, author);
        messageRepository.save(message);

        BinaryContent attachment = new BinaryContent("file.txt", 100L, "text/plain");
        binaryContentRepository.save(attachment);

        MessageAttachment messageAttachment = new MessageAttachment(message, attachment, 0);
        messageAttachmentRepository.save(messageAttachment);

        UUID messageId = message.getId();

        // 작성자로 보안 컨텍스트 설정
        setSecurityContextForUser(author);

        // when
        mockMvc.perform(delete("/api/messages/{messageId}", messageId)
                .with(csrf()))
            .andExpect(status().isNoContent());

        // then - 메시지와 첨부파일이 모두 삭제되었는지 확인
        assertThat(messageRepository.findById(messageId)).isEmpty();
        assertThat(messageAttachmentRepository.findAttachmentsByMessageId(messageId)).isEmpty();
        // BinaryContent는 orphan cleanup에 의해 나중에 삭제됨
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("메시지 삭제 - 실패: 존재하지 않는 메시지 삭제 시도")
    void deleteMessage_NotFound_Fails() throws Exception {
        // given
        UUID nonExistentId = UUID.randomUUID();

        // when & then - 작성자가 아니므로 AccessDenied (403)
        mockMvc.perform(delete("/api/messages/{messageId}", nonExistentId)
                .with(csrf()))
            .andExpect(status().isForbidden());
    }
}
