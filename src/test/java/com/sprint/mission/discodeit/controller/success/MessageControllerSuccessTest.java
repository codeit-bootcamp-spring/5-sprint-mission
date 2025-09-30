package com.sprint.mission.discodeit.controller.success;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.domain.binarycontent.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.domain.message.MessageController;
import com.sprint.mission.discodeit.domain.message.MessageService;
import com.sprint.mission.discodeit.domain.message.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.domain.message.dto.MessageDto;
import com.sprint.mission.discodeit.domain.message.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.domain.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = MessageController.class)
public class MessageControllerSuccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext wac;

    private UUID messageId;
    private UUID channelId;
    private UUID authorId;
    private UserDto authorDto;
    private MessageDto messageDto;

    @BeforeEach
    void setUp() {
        // 한글 처리 설정
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();

        // 테스트 데이터 초기화
        messageId = UUID.randomUUID();
        channelId = UUID.randomUUID();
        authorId = UUID.randomUUID();
        authorDto = UserDto.builder().id(authorId).email("testUser@test.com").username("testUser").build();
        messageDto = MessageDto.builder().id(messageId).author(authorDto).channelId(channelId).content("테스트 메시지").build();
    }

    @Test
    @DisplayName("메시지 생성 성공 테스트")
    void createMessage_Success() throws Exception {
        // Given
        MessageCreateRequest createRequest = new MessageCreateRequest("테스트 메시지", channelId, authorId); // 메시지
        BDDMockito.given(messageService.create(any(MessageCreateRequest.class), anyList())).willReturn(messageDto);

        MockMultipartFile message = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(createRequest).getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile attachment = new MockMultipartFile(
                "attachments",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "This is a test file.".getBytes(StandardCharsets.UTF_8)
        );

        // when
        mockMvc.perform(multipart("/api/messages")
                        .file(message)
                        .file(attachment))
                // then
                .andExpect(status().isCreated()) // 201 Created 상태 코드를 기대
                .andExpect(jsonPath("$.id").value(messageId.toString())) // 응답 JSON의 id 필드 검증
                .andExpect(jsonPath("$.content").value("테스트 메시지")) // content 필드 검증
                .andExpect(jsonPath("$.author.id").value(authorId.toString())); // author.id 필드 검증
    }

    @Test
    @DisplayName("메시지 수정 성공 테스트")
    void updateMessage_Success() throws Exception {
        // Given
        MessageUpdateRequest updateRequest = new MessageUpdateRequest("수정된 메시지 내용");
        MessageDto updatedDto = MessageDto.builder().id(messageId).author(authorDto).channelId(channelId).content("수정된 메시지 내용").build();


        BDDMockito.given(messageService.update(any(UUID.class),any(MessageUpdateRequest.class))).willReturn(updatedDto);

        // when
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                )
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(messageId.toString())) // id 필드 검증
                .andExpect(jsonPath("$.content").value("수정된 메시지 내용")); // 수정된 content 필드 검증
    }
}
