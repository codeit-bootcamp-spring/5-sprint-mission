package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "유저 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  //회원가입
  @Operation(summary = "회원가입")
  @PostMapping(consumes = "multipart/form-data")
  public ResponseEntity<User> create(
      @RequestPart("userCreateRequest") UserCreateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    User createdUser = userService.create(request, profile);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  //유저 전체조회
  @Operation(summary = "유저 전체조회")
  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    return ResponseEntity.ok(userService.findAll());
  }


  //유저 단건조회
  @Operation(summary = "유저 단건조회")
  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> findById(@PathVariable UUID userId) {
    return ResponseEntity.ok(userService.findById(userId));
  }

  // 사용자 수정 : username,email,password
  @Operation(summary = "회원 정보 수정")
  @PatchMapping(value = "/{userId}", consumes = "multipart/form-data")
  public ResponseEntity<User> update(
      @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) throws IOException {
    User updatedUser = userService.update(userId, request, profile);
    return ResponseEntity.ok(updatedUser);
  }


  // 사용자 삭제
  @Operation(summary = "회원 탈퇴")
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }
}
