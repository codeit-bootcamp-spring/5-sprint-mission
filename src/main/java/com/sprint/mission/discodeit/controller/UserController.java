package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest; // UserUpdateRequest 누락되어 추가
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid; // @Valid 누락되어 추가
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.annotation.Validated; // @Validated 누락되어 추가

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Validated // 유효성 검사를 위해 추가
public class UserController implements UserApi {

  private final UserService userService;
  private final UserStatusService userStatusService;

  // DTO 통일을 위해 UserResponseDto 대신 UserDto 사용
  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @Override
  public ResponseEntity<UserDto> create(
      @RequestPart("userCreateRequest") @Valid UserCreateRequest userCreateRequest, // @Valid 추가
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    log.info("[USER][CREATE] username={}, email={}", userCreateRequest.username(), userCreateRequest.email());
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    UserDto createdUser = userService.create(userCreateRequest, profileRequest);
    log.debug("[USER][CREATE][DONE] id={}", createdUser.id());
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdUser);
  }

  // DTO 통일을 위해 UserResponseDto 대신 UserDto 사용
  @PatchMapping(
      path = "{userId}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  @Override
  public ResponseEntity<UserDto> update(
      @PathVariable("userId") UUID userId,
      @RequestPart("userUpdateRequest") @Valid UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    log.info("[USER][UPDATE] id={}, hasProfile={}", userId, profileRequest.isPresent());
    UserDto updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
    log.debug("[USER][UPDATE][DONE] id={}", updatedUser.id());
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedUser);
  }

  @DeleteMapping(path = "{userId}")
  @Override
  public ResponseEntity<Void> delete(@PathVariable("userId") UUID userId) {
    log.warn("[USER][DELETE] id={}", userId);
    userService.delete(userId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @GetMapping
  @Override
  public ResponseEntity<List<UserDto>> findAll() {
    log.debug("[USER][LIST]");
    List<UserDto> users = userService.findAll();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(users);
  }

  @PatchMapping(path = "{userId}/userStatus")
  @Override
  public ResponseEntity<UserStatusDto> updateUserStatusByUserId(@PathVariable("userId") UUID userId,
      @RequestBody @Valid UserStatusUpdateRequest request) {
    log.info("[USER][UPDATE][USER_STATUS] id = {}", userId);
    UserStatusDto updatedUserStatus = userStatusService.updateByUserId(userId, request);
    log.debug("[USER][UPDATE][USER_STATUS][DONE] id = {}", updatedUserStatus.id());
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedUserStatus);
  }

  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
    log.info("[USER][PROFILE] name={}", profileFile.getOriginalFilename());
    if (profileFile.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            profileFile.getOriginalFilename(),
            profileFile.getContentType(),
            profileFile.getBytes()
        );
        log.warn("[USER][PROFILE][DONE] name={}", profileFile.getOriginalFilename());
        return Optional.of(binaryContentCreateRequest);
      } catch (IOException e) {
        log.error("[USER][PROFILE][ERROR] name={}", profileFile.getOriginalFilename(), e);
        throw new RuntimeException("Failed to read profile file content.", e);
      }
    }
  }
}