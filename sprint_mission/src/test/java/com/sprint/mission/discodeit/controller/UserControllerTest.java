package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;



@WebMvcTest(UserController.class)
//@AutoConfigureMockMvc(addFilters = false) // MockMvc 설정을 도와주는 어노테이션
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;  // http 요청과 응답을 처리해줄 mock 객체

    @Autowired
    private ObjectMapper objectMapper; // json 처리를 도와줄 Mapper

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserStatusService userStatusService;

//    @MockitoBean
//    private JpaMetamodelMappingContext jpaMetamodelMappingContext; // 가상의 jpa을 실행해줄 환경



    @Test
    void createUserTest_without_profile() throws Exception {
        UserCreateRequest request = new UserCreateRequest("test1","test1@test.com","12341234");
        UUID userId = UUID.randomUUID();
        //응답설계
        UserDto response = new UserDto(userId,"test1","test1@test.com",null, true);
        String requestJson = objectMapper.writeValueAsString(request); // json변환
        MockMultipartFile userPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                requestJson.getBytes()
        );

        given(userService.create(eq(request),any())).willReturn(response);

        mockMvc.perform(multipart("/api/users")
                        .file(userPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("test1"));
    }


}
