package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController implements UserApi {

  private final UserService userService;
  private final UserStatusService userStatusService;
  private final UserMapper userMapper;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Override
  public ResponseEntity<UserDto> create(
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    var profileRequest = resolveProfileRequest(profile);
    UserDto createdUser = userService.create(userCreateRequest, profileRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @PatchMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Override
  public ResponseEntity<UserDto> update(
      @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    var profileRequest = resolveProfileRequest(profile);
    UserDto updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
    return ResponseEntity.ok(updatedUser);
  }

  @DeleteMapping("/{userId}")
  @Override
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @Override
  public ResponseEntity<List<UserDto>> findAll() {
    return ResponseEntity.ok(userService.findAll());
  }

  // 여기만 @PatchMapping 메서드를 이걸로 교체
  @RequestMapping(path = "/{userId}/userStatus", method = {RequestMethod.PUT, RequestMethod.PATCH})
  @Override
  public ResponseEntity<UserDto> updateUserStatusByUserId(
      @PathVariable UUID userId,
      @RequestBody(required = false) @Nullable UserStatusUpdateRequest request
  ) {
    var status = userStatusService.updateByUserId(userId, request);
    return ResponseEntity.ok(userMapper.toDto(status.getUser(), true));
  }

  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
    if (profileFile == null || profileFile.isEmpty()) return Optional.empty();
    try {
      return Optional.of(new BinaryContentCreateRequest(
          profileFile.getOriginalFilename(),
          profileFile.getContentType(),
          profileFile.getBytes()
      ));
    } catch (IOException e) {
      throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Failed to read profile file", e);
    }
  }
}
