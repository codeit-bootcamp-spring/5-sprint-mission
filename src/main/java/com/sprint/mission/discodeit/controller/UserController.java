package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.CreateRequest;
import com.sprint.mission.discodeit.dto.UserDto.Detail;
import com.sprint.mission.discodeit.dto.UserDto.UpdateRequest;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @PostMapping
  public ResponseEntity<UserDto.DetailResponse> createUser(
      @RequestPart("userCreateRequest") CreateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(userService.create(request.toCreate(profile)).toDetailResponse());
  }

  @PatchMapping("/{id}")
  public ResponseEntity<UserDto.DetailResponse> updateUser(@PathVariable UUID id,
      @RequestPart("userUpdateRequest") UpdateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {

    return ResponseEntity.ok(userService.update(request.toUpdate(id, profile)).toDetailResponse());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {

    userService.delete(id);

    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<UserDto.DetailResponse>> findAllUsers() {

    return ResponseEntity.ok(userService.findAll().stream().map(Detail::toDetailResponse).toList());
  }

  @PatchMapping("/{id}/userStatus")
  public ResponseEntity<UserStatusDto.DetailResponse> updateUserStatus(@PathVariable UUID id) {

    return ResponseEntity.ok(userStatusService.updateByUserId(id).toDetailResponse());
  }
}
