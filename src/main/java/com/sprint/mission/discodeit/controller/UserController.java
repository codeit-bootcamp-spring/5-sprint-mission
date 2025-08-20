package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserFindResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

  private final UserService userService;
  private final BasicUserStatusService userStatusService;

  @Operation(summary = "User 등록")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨"),
      @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(examples = @ExampleObject(value = "User with email {email} already exists")))
  })
  @Parameter(name = "profile", description = "User 프로필 이미지")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<User> create(
      @RequestPart UserCreateRequest userCreateRequest,
      @RequestPart(required = false) MultipartFile profile
  ) throws IOException {
    Optional<BinaryContentCreateRequest> binaryContentCreateRequest = Optional.empty();
    if (profile != null && !profile.isEmpty()) {
      binaryContentCreateRequest = Optional.of(new BinaryContentCreateRequest(
          profile.getOriginalFilename(),
          profile.getContentType(),
          profile.getBytes()
      ));
    }
    User createdUser = userService.create(userCreateRequest, binaryContentCreateRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @Operation(summary = "User 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨"),
      @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(examples = @ExampleObject(value = "user with email {newEmail} already exists"))),
      @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "User with id {userId} not found")))
  })
  @Parameter(name = "userId", description = "수정할 User ID")
  @Parameter(name = "profile", description = "수정할 User 프로필 이미지")
  @PatchMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<User> update(
      @PathVariable UUID userId,
      @RequestPart UserUpdateRequest userUpdateRequest,
      @RequestPart(required = false) MultipartFile profile
  ) throws IOException {
    Optional<BinaryContentCreateRequest> binaryContentCreateRequest = Optional.empty();
    if (profile != null && !profile.isEmpty()) {
      binaryContentCreateRequest = Optional.of(new BinaryContentCreateRequest(
          profile.getOriginalFilename(),
          profile.getContentType(),
          profile.getBytes()
      ));
    }
    User updatedUser = userService.update(userId, userUpdateRequest, binaryContentCreateRequest);
    return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
  }

  @Operation(summary = "User 삭제")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "User with id {id} not found")))
  })
  @Parameter(name = "userId", description = "삭제할 User ID")
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "전체 User 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User 목록 조회 성공")
  })
  @GetMapping
  public ResponseEntity<List<UserFindResponse>> findAll() {
    List<UserFindResponse> users = userService.findAll();
    return ResponseEntity.status(HttpStatus.OK).body(users);
  }

  @Operation(summary = "User 온라인 상태 업데이트")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨"),
      @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "UserStatus with userId {userId} not found")))
  })
  @Parameter(name = "userId", description = "상태를 변경할 User ID")
  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatus> updateUserStatusByUserId(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest userStatusUpdateRequest) {
    UserStatus updatedStatus = userStatusService.updateByUserId(userId, userStatusUpdateRequest);
    return ResponseEntity.status(HttpStatus.OK).body(updatedStatus);
  }
}
