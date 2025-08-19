package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserFindResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "User API")
public class UserController {

  private final BasicUserService userService;
  private final BasicUserStatusService userStatusService;

  @Operation(summary = "User 등록")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<User> create(
      @RequestPart UserCreateRequest userCreateRequest,
      @RequestPart(required = false) MultipartFile profileImage
  ) throws IOException {
    Optional<BinaryContentCreateRequest> binaryContentCreateRequest = Optional.empty();
    if (profileImage != null && !profileImage.isEmpty()) {
      binaryContentCreateRequest = Optional.of(new BinaryContentCreateRequest(
          profileImage.getOriginalFilename(),
          profileImage.getContentType(),
          profileImage.getBytes()
      ));
    }
    User createdUser = userService.create(userCreateRequest, binaryContentCreateRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @Operation(summary = "User 정보 수정")
  @PatchMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<User> update(
      @PathVariable UUID userId,
      @RequestPart UserUpdateRequest userUpdateRequest,
      @RequestPart(required = false) MultipartFile profileImage
  ) throws IOException {
    Optional<BinaryContentCreateRequest> binaryContentCreateRequest = Optional.empty();
    if (profileImage != null && !profileImage.isEmpty()) {
      binaryContentCreateRequest = Optional.of(new BinaryContentCreateRequest(
          profileImage.getOriginalFilename(),
          profileImage.getContentType(),
          profileImage.getBytes()
      ));
    }
    User updatedUser = userService.update(userId, userUpdateRequest, binaryContentCreateRequest);
    return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
  }

  @Operation(summary = "User 삭제")
  @DeleteMapping("/{userId}")
  public ResponseEntity<User> delete(@PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "전체 User 목록 조회")
  @GetMapping
  public ResponseEntity<List<UserFindResponse>> findAll() {
    List<UserFindResponse> users = userService.findAll();
    return ResponseEntity.status(HttpStatus.OK).body(users);
  }

  @Operation(summary = "User 온라인 상태 업데이트")
  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatus> updateUserStatus(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest userStatusUpdateRequest) {
    UserStatus updatedStatus = userStatusService.updateByUserId(userId, userStatusUpdateRequest);
    return ResponseEntity.status(HttpStatus.OK).body(updatedStatus);
  }
}
