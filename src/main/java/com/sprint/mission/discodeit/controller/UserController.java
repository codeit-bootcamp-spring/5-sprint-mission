package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Tag(name = "User", description = "유저 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;


  //회원가입
  @Operation(summary = "회원가입")
  @PostMapping(consumes = "multipart/form-data")
  public ResponseEntity<UserDto> create(
      @RequestPart("userDto") UserDto dto,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) throws IOException {
    UserDto responseDto = userService.create(dto, profile);
    log.info("회원가입 완료: userId={}", responseDto.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }

  //유저 조회
  @Operation(summary = "유저 전체조회")
  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    log.info("유저 전체 조회 요청");
    List<UserDto> users = userService.findAll();
    return ResponseEntity.ok(users);
  }

  //단건조회
  @Operation(summary = "유저 단건조회")
  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> findById(@PathVariable("userId") UUID id) {
    log.info("유저 단건 조회 요청: userId={}", id);
    UserDto user = userService.findById(id);
    return ResponseEntity.ok(user);
  }

  //정보 수정
  @Operation(summary = "회원 정보 수정")
  @PatchMapping(value = "/{userId}", consumes = "multipart/form-data")
  public ResponseEntity<UserDto> update(
      @PathVariable("userId") UUID id,
      @RequestPart("userDto") UserDto dto,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) throws IOException {
    UserDto updatedUser = userService.update(id, dto, profile);
    log.info("회원 정보 수정 완료: userId={}", id);
    return ResponseEntity.ok(updatedUser);
  }

  //회원 탈퇴
  @Operation(summary = "회원 탈퇴")
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> delete(@PathVariable("userId") UUID id) {
    userService.delete(id);
    log.info("회원 탈퇴 완료: userId={}", id);
    return ResponseEntity.noContent().build();
  }
}