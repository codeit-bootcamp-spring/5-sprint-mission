package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @PostMapping
  public ResponseEntity<UserDto.DetailResponse> createUser(
      @RequestPart("userCreateRequest") UserDto.Request request,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {

    return ResponseEntity.ok(userService.create(request.toCreate(profile)));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<UserDto.DetailResponse> updateUser(
      @PathVariable UUID id,
      @RequestPart("userUpdateRequest") UserDto.Request request,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {

    return ResponseEntity.ok(userService.update(request.toUpdate(id, profile)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {

    userService.delete(id);

    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<UserDto.DetailResponse>> findAllUsers() {

    return ResponseEntity.ok(userService.findAll());
  }

  @PatchMapping("/{id}/userStatus")
  public ResponseEntity<Void> updateUserStatus(@PathVariable UUID id) {

    userStatusService.updateByUserId(id);

    return ResponseEntity.ok().build();
  }
}
