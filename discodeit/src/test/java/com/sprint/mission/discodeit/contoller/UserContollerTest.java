package com.sprint.mission.discodeit.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.UserController;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.boot.test.context.TestConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;



import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
public class UserContollerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @TestConfiguration
    static class Config {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
        @Bean
        public UserStatusService userStatusService() {
            return Mockito.mock(UserStatusService.class);
        }
    }
    @Autowired
    UserService userService;
    @Autowired
    UserStatusService userStatusService;



    @Test
    void createUser_returns201() throws Exception {
        UserCreateRequest req = new UserCreateRequest("user1", "test2@email.com","user123");

        when(userService.create(any(UserCreateRequest.class), any()))
                .thenReturn(new UserDto(
                        UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                        "user1",
                        "test2@email.com",
                        null,
                        false
                ));


        MockMultipartFile userPart =
                new MockMultipartFile("userCreateRequest", "",
                        "application/json", mapper.writeValueAsBytes(req));

        mockMvc.perform(multipart("/api/users")
                        .file(userPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.email").value("test2@email.com"))
                .andExpect(jsonPath("$.profile").doesNotExist()) // null이면 JSON에서 아예 안 내려옴
                .andExpect(jsonPath("$.online").value(false));


        //JSON만 사용할 때 사용하는 코드
//        mockMvc.perform(post("/api/users")
//                        .content(mapper.writeValueAsString(req))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated());
    }





}
