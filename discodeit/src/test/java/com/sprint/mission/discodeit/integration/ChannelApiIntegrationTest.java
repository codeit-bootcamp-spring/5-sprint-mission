package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
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

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ChannelApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("채널 생성 - PUBLIC")
    void createPublicChannel_success() throws Exception {
        // given
        PublicChannelCreateRequest channelReq = new PublicChannelCreateRequest(
                "bus","bustam");

        mockMvc.perform(post("/api/channels/public")
                    .contentType(MediaType.APPLICATION_JSON)   // ✅ JSON 타입 지정
                    .content(objectMapper.writeValueAsString(channelReq))) // ✅ 요청 body 추가
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("bus"))
                .andExpect(jsonPath("$.description").value("bustam"));

    }

    @Test
    @DisplayName("사용자 삭제 - 성공 후 메시지 확인")
    void deleteChannel_success() throws Exception {

        // given: 한 명 생성
        Channel channel = new Channel(ChannelType.PUBLIC,"bus","bustam");
        channelRepository.saveAndFlush(channel);


        // when & then
        mockMvc.perform(delete("/api/channels/{id}", channel.getId()))
                .andExpect(status().isNoContent())   // ✅ 204 응답 검사
                .andExpect(content().string(""));    // ✅ body 비어 있는지 확인
    }
}

