package com.sprint.mission.discodeit.controller.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.domain.binarycontent.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.domain.message.MessageController;
import com.sprint.mission.discodeit.domain.message.MessageService;
import com.sprint.mission.discodeit.domain.message.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.domain.message.dto.MessageDto;
import com.sprint.mission.discodeit.domain.message.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.domain.message.exception.MessageNotFoundException;
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


import java.util.UUID;

@WebMvcTest(controllers = MessageController.class)
public class MessageControllerFailTest {

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

    @Test
    @DisplayName("메시지 수정 실패 테스트 - 존재하지 않는 메시지")
    void updateMessage_Fail_NotFound() throws Exception {
        // given
        UUID nonExistentMessageId = UUID.randomUUID();
        MessageUpdateRequest updateRequest = new MessageUpdateRequest("이 내용은 반영되지 않음");

        // messageService.update 메소드가 MessageNotFoundException을 던지도록 설정
        BDDMockito.given(messageService.update(any(UUID.class),any(MessageUpdateRequest.class)))
                        .willThrow(new MessageNotFoundException(nonExistentMessageId));

        // when
        mockMvc.perform(patch("/api/messages/{messageId}", nonExistentMessageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                )
                // then
                .andExpect(status().isNotFound()); // 404 Not Found 상태 코드를 기대
    }
}
