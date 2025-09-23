package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private UserStatusService userStatusService;

  @MockitoBean
  private UserMapper userMapper;

  @MockitoBean
  private UserStatusMapper userStatusMapper;

  @MockitoBean
  private BinaryContentMapper binaryContentMapper;

  @MockitoBean
  private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  private UserDto.Detail userDetail;
  private UserDto.DetailResponse userDetailResponse;
  private UserStatusDto.Detail userStatusDetail;
  private UserStatusDto.DetailResponse userStatusDetailResponse;

  @BeforeEach
  void setup() {
    UUID userId = UUID.randomUUID();
    Instant fixedInstant = Instant.parse("2025-09-22T00:00:00Z"); // 고정 Instant

    userDetail = UserDto.Detail.builder()
                               .id(userId)
                               .username("user1")
                               .email("user1@email.com")
                               .online(true)
                               .build();

    userDetailResponse = UserDto.DetailResponse.builder()
                                               .id(userId)
                                               .username("user1")
                                               .email("user1@email.com")
                                               .online(true)
                                               .build();

    userStatusDetail = UserStatusDto.Detail.builder()
                                           .userId(userId)
                                           .lastActiveAt(fixedInstant)
                                           .build();

    userStatusDetailResponse = UserStatusDto.DetailResponse.builder()
                                                           .userId(userId)
                                                           .lastActiveAt(fixedInstant)
                                                           .build();
  }

  @Test
  void testGetUsers() throws Exception {
    when(userService.findAll()).thenReturn(Arrays.asList(userDetail));
    when(userMapper.toDetailResponse(any())).thenReturn(userDetailResponse);

    mockMvc.perform(get("/api/users").contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].username").value(userDetail.getUsername()));
  }

  @Test
  void testCreateUser() throws Exception {
    MockMultipartFile userPart = new MockMultipartFile("userCreateRequest", "", "application/json",
        objectMapper.writeValueAsBytes(UserDto.CreateRequest.builder()
                                                            .username("user1")
                                                            .email("user1@email.com")
                                                            .password("password")
                                                            .build()));
    MockMultipartFile profilePart = new MockMultipartFile("profile", "profile.png", "image/png",
        "dummy".getBytes());

    when(userService.create(any())).thenReturn(userDetail);
    when(userMapper.toDetailResponse(any())).thenReturn(userDetailResponse);

    mockMvc.perform(multipart("/api/users").file(userPart)
                                           .file(profilePart)
                                           .contentType(MediaType.MULTIPART_FORM_DATA))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.username").value(userDetail.getUsername()));
  }

  @Test
  void testUpdateUser() throws Exception {
    UUID userId = userDetail.getId();
    MockMultipartFile userPart = new MockMultipartFile("userUpdateRequest", "", "application/json",
        objectMapper.writeValueAsBytes(UserDto.UpdateRequest.builder()
                                                            .newUsername("user1_updated")
                                                            .newEmail("user1_updated@email.com")
                                                            .build()));
    MockMultipartFile profilePart = new MockMultipartFile("profile", "profile.png", "image/png",
        "dummy".getBytes());

    when(userService.update(any())).thenReturn(userDetail);
    when(userMapper.toDetailResponse(any())).thenReturn(userDetailResponse);

    mockMvc.perform(multipart("/api/users/" + userId).file(userPart)
                                                     .file(profilePart)
                                                     .with(request -> {
                                                       request.setMethod("PATCH");
                                                       return request;
                                                     }) // PATCH 처리
                                                     .contentType(MediaType.MULTIPART_FORM_DATA))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.username").value(userDetail.getUsername()));
  }

  @Test
  void testDeleteUser() throws Exception {
    UUID userId = userDetail.getId();
    mockMvc.perform(delete("/api/users/" + userId))
           .andExpect(status().isNoContent());
  }

  @Test
  void testUpdateUserStatus() throws Exception {
    UUID userId = userDetail.getId();
    when(userStatusService.updateByUserId(eq(userId))).thenReturn(userStatusDetail);
    when(userStatusMapper.toDetailResponse(any())).thenReturn(
        userStatusDetailResponse);

    mockMvc.perform(patch("/api/users/" + userId + "/userStatus"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.lastActiveAt").value(userStatusDetailResponse.getLastActiveAt()
                                                                               .toString()));
  }
}
