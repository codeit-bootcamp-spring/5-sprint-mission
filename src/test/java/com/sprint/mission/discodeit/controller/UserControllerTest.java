package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.UserAlreadyExistsException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class UserControllerTest {

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;

  @SuppressWarnings({"removal","deprecation"})
  @org.springframework.boot.test.mock.mockito.MockBean
  UserService userService;

  @SuppressWarnings({"removal","deprecation"})
  @org.springframework.boot.test.mock.mockito.MockBean
  UserStatusService userStatusService;

  @Test
  void create_success_without_profile() throws Exception {
    // JSON 파트 준비
    var reqObj = new UserCreateRequest("neo","neo@matrix.io","secret123");
    var json = om.writeValueAsString(reqObj);
    var userPart = new MockMultipartFile(
        "userCreateRequest", "user.json", MediaType.APPLICATION_JSON_VALUE, json.getBytes(StandardCharsets.UTF_8)
    );

    // 서비스 스텁
    var dto = new UserDto(UUID.randomUUID(), "neo", "neo@matrix.io", null, true);
    given(userService.create(eq(reqObj), any(Optional.class))).willReturn(dto);

    mvc.perform(multipart("/api/users").file(userPart))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("neo"))
        .andExpect(jsonPath("$.email").value("neo@matrix.io"));
  }

  @Test
  void create_success_with_profile() throws Exception {
    var reqObj = new UserCreateRequest("trinity","tri@matrix.io","secret123");
    var json = om.writeValueAsString(reqObj);
    var userPart = new MockMultipartFile(
        "userCreateRequest", "user.json", MediaType.APPLICATION_JSON_VALUE, json.getBytes(StandardCharsets.UTF_8)
    );
    var profile = new MockMultipartFile(
        "profile", "pic.png", "image/png", "PNGDATA".getBytes(StandardCharsets.UTF_8)
    );

    var dto = new UserDto(UUID.randomUUID(), "trinity", "tri@matrix.io", null, true);
    // 프로필이 존재하는 Optional을 기대
    given(userService.create(eq(reqObj), argThat(opt -> opt != null && opt.isPresent()))).willReturn(dto);

    mvc.perform(multipart("/api/users").file(userPart).file(profile))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("trinity"));
  }

  @Test
  void create_fail_conflict() throws Exception {
    var reqObj = new UserCreateRequest("neo","neo@matrix.io","secret123");
    var json = om.writeValueAsString(reqObj);
    var userPart = new MockMultipartFile(
        "userCreateRequest", "user.json", MediaType.APPLICATION_JSON_VALUE, json.getBytes(StandardCharsets.UTF_8)
    );

    willThrow(new UserAlreadyExistsException("neo@matrix.io"))
        .given(userService).create(eq(reqObj), any(Optional.class));

    mvc.perform(multipart("/api/users").file(userPart))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("USER_ALREADY_EXISTS"));
  }

  @Test
  void update_success_with_profile() throws Exception {
    var userId = UUID.randomUUID();
    var reqObj = new UserUpdateRequest("newName","new@mail.io","newpass");
    var json = om.writeValueAsString(reqObj);
    var userPart = new MockMultipartFile(
        "userUpdateRequest", "update.json", MediaType.APPLICATION_JSON_VALUE, json.getBytes(StandardCharsets.UTF_8)
    );
    var profile = new MockMultipartFile(
        "profile", "pic2.png", "image/png", "IMG".getBytes(StandardCharsets.UTF_8)
    );

    var dto = new UserDto(userId, "newName", "new@mail.io", null, true);
    given(userService.update(eq(userId), eq(reqObj), argThat(opt -> opt != null && opt.isPresent()))).willReturn(dto);

    mvc.perform(multipart("/api/users/{userId}", userId)
            .file(userPart)
            .file(profile)
            .with(r -> { r.setMethod("PATCH"); return r; }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("newName"));
  }

  @Test
  void delete_success() throws Exception {
    var userId = UUID.randomUUID();
    willDoNothing().given(userService).delete(eq(userId));

    mvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());
  }

  @Test
  void findAll_success() throws Exception {
    var u1 = new UserDto(UUID.randomUUID(), "neo", "neo@matrix.io", null, true);
    var u2 = new UserDto(UUID.randomUUID(), "trinity", "tri@matrix.io", null, false);
    given(userService.findAll()).willReturn(List.of(u1, u2));

    mvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value("neo"))
        .andExpect(jsonPath("$[1].username").value("trinity"));
  }

  @Test
  void updateUserStatusByUserId_success() throws Exception {
    var userId = UUID.randomUUID();
    var when = Instant.parse("2025-01-02T00:00:00Z");
    var req = new UserStatusUpdateRequest(when);
    var dto = new UserStatusDto(UUID.randomUUID(), userId, when);

    given(userStatusService.updateByUserId(eq(userId), eq(req))).willReturn(dto);

    mvc.perform(patch("/api/users/{userId}/userStatus", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(userId.toString()))
        .andExpect(jsonPath("$.lastActiveAt").value("2025-01-02T00:00:00Z"));
  }
}
