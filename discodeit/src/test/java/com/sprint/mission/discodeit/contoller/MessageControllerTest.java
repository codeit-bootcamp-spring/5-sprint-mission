package com.sprint.mission.discodeit.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.MessageController;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @TestConfiguration
    static class Config {
        @Bean
        public MessageService messageService() {
            return Mockito.mock(MessageService.class);
        }

    }
    @Autowired
    MessageService messageService;


    @Test
    void createMessage_returns201() throws Exception {
        UUID channelId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        MessageCreateRequest req = new MessageCreateRequest("bus", channelId, userId);

        MockMultipartFile messagePart =
                new MockMultipartFile("messageCreateRequest", "",
                        "application/json", mapper.writeValueAsBytes(req));

        MockMultipartFile filePart = new MockMultipartFile(
                "attachments",                   // 컨트롤러에서 @RequestPart에 지정한 이름
                "test.png",                      // 파일명
                "image/png",                     // content type
                "dummy-image-content".getBytes() // 파일 내용
        );


        when(messageService.create(any(MessageCreateRequest.class), anyList()))
                .thenReturn(new MessageDto(
                        UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                        null,
                        null,
                        "bus",
                        channelId,
                        new UserDto(userId,"bus","bus@mail.com",null,false),
                        null
                ));


        mockMvc.perform(multipart("/api/messages")
                        .file(messagePart)
                        .file(filePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(jsonPath("$.createdAt").doesNotExist())
                .andExpect(jsonPath("$.updatedAt").doesNotExist())
                .andExpect(jsonPath("$.content").value("bus")) // null이면 JSON에서 아예 안 내려옴
                .andExpect(jsonPath("$.channelId").value(channelId.toString()))
                .andExpect(jsonPath("$.author.id").value(userId.toString()))
                .andExpect(jsonPath("$.author.username").value("bus"))
                .andExpect(jsonPath("$.author.email").value("bus@mail.com"))
                .andExpect(jsonPath("$.attachments").doesNotExist());


    }

}
