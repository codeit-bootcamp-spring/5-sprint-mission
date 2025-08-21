package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.dto.response.UserStatusResponseDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponseDto> create(
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) throws IOException {

    Optional<BinaryContentCreateRequest> optionalProfile = Optional.empty();

    if (profile != null && !profile.isEmpty()) {
      optionalProfile = Optional.of(new BinaryContentCreateRequest(
          profile.getOriginalFilename(),
          profile.getContentType(),
          profile.getBytes()
      ));
    }

    UserResponseDto response = userService.create(userCreateRequest, optionalProfile);
    return ResponseEntity.ok(response);
  }

  @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponseDto> update(
      @PathVariable("userId") UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) throws IOException {

    Optional<BinaryContentCreateRequest> optionalProfile = Optional.empty();

    if (profile != null && !profile.isEmpty()) {
      optionalProfile = Optional.of(new BinaryContentCreateRequest(
          profile.getOriginalFilename(),
          profile.getContentType(),
          profile.getBytes()
      ));
    }
    UserResponseDto response = userService.update(userId, userUpdateRequest, optionalProfile);
    return ResponseEntity.ok(response);
  }


  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> delete(@PathVariable("userId") UUID userId) {
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<UserResponseDto>> findAll() {
    return ResponseEntity.ok(userService.findAll());
  }

  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatusResponseDto> updateStatus(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest request
  ) {
    return ResponseEntity.ok(userStatusService.updateByUserId(userId, request));
  }
}

