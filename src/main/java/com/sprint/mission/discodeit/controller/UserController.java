package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController implements UserApi {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @Override
  public ResponseEntity<UserDto> create(
          @Validated @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
          @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    log.info("사용자 생성 요청 수신: username={}, email={}",
            userCreateRequest.username(), userCreateRequest.email());

    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest);

    UserDto createdUser = userService.create(userCreateRequest, profileRequest);

    log.info("사용자 생성 완료: userId={}", createdUser.id());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @PatchMapping(
          path = "{userId}",
          consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  @Override
  public ResponseEntity<UserDto> update(
          @PathVariable("userId") UUID userId,
          @Validated @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
          @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    log.info("사용자 수정 요청 수신: userId={}", userId);

    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest);

    UserDto updatedUser = userService.update(userId, userUpdateRequest, profileRequest);

    log.info("사용자 수정 완료: userId={}", updatedUser.id());
    return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
  }

  @DeleteMapping(path = "{userId}")
  @Override
  public ResponseEntity<Void> delete(@PathVariable("userId") UUID userId) {
    log.info("사용자 삭제 요청 수신: userId={}", userId);

    userService.delete(userId);

    log.info("사용자 삭제 완료: userId={}", userId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping
  @Override
  public ResponseEntity<List<UserDto>> findAll() {
    log.info("모든 사용자 조회 요청 수신");

    List<UserDto> users = userService.findAll();

    log.info("모든 사용자 조회 완료: count={}", users.size());
    return ResponseEntity.status(HttpStatus.OK).body(users);
  }

  @PatchMapping(path = "{userId}/userStatus")
  @Override
  public ResponseEntity<UserStatusDto> updateUserStatusByUserId(
          @PathVariable("userId") UUID userId,
          @Validated @RequestBody UserStatusUpdateRequest request
  ) {
    log.info("사용자 상태 업데이트 요청 수신: userId={}, newLastActiveAt={}",
            userId, request.newLastActiveAt());

    UserStatusDto updatedUserStatus = userStatusService.updateByUserId(userId, request);

    log.info("사용자 상태 업데이트 완료: userStatusId={}", updatedUserStatus.id());
    return ResponseEntity.status(HttpStatus.OK).body(updatedUserStatus);
  }

  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
    if (profileFile.isEmpty()) {
      log.warn("프로필 파일이 비어있음");
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                profileFile.getOriginalFilename(),
                profileFile.getContentType(),
                profileFile.getBytes()
        );
        log.debug("프로필 파일 변환 완료: name={}, contentType={}",
                profileFile.getOriginalFilename(), profileFile.getContentType());
        return Optional.of(binaryContentCreateRequest);
      } catch (IOException e) {
        log.error("프로필 파일 변환 실패", e);
        throw new RuntimeException(e);
      }
    }
  }
}