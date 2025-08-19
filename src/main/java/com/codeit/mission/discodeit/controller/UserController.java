package com.codeit.mission.discodeit.controller;

import com.codeit.mission.discodeit.dto.data.UserDto;
import com.codeit.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.codeit.mission.discodeit.dto.request.UserCreateRequest;
import com.codeit.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.codeit.mission.discodeit.dto.request.UserUpdateRequest;
import com.codeit.mission.discodeit.entity.User;
import com.codeit.mission.discodeit.entity.UserStatus;
import com.codeit.mission.discodeit.service.UserService;
import com.codeit.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final UserStatusService userStatusService;

    @PostMapping
    public ResponseEntity<User> create(
        @RequestPart UserCreateRequest userCreateRequest,
        @RequestPart(required = false) MultipartFile profile
    ) throws IOException {
        if (userCreateRequest == null) {
            throw new IllegalArgumentException("userCreateRequest가 필요합니다.");
        }
        if (userCreateRequest.username() == null || userCreateRequest.username().trim().isEmpty()) {
            throw new IllegalArgumentException("username이 필요합니다.");
        }
        if (userCreateRequest.email() == null || userCreateRequest.email().trim().isEmpty()) {
            throw new IllegalArgumentException("email이 필요합니다.");
        }
        if (userCreateRequest.password() == null || userCreateRequest.password().trim().isEmpty()) {
            throw new IllegalArgumentException("password가 필요합니다.");
        }

        Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.empty();
        if (profile != null && !profile.isEmpty()) {
            profileCreateRequest = Optional.of(new BinaryContentCreateRequest(
                profile.getOriginalFilename(),
                profile.getContentType(),
                profile.getBytes()
            ));
        }
        User user = userService.create(userCreateRequest, profileCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<User> update(
        @PathVariable UUID userId,
        @RequestPart UserUpdateRequest userUpdateRequest,
        @RequestPart(required = false) MultipartFile profile
    ) throws IOException {
        if (userId == null) {
            throw new IllegalArgumentException("userId가 필요합니다.");
        }
        if (userUpdateRequest == null) {
            throw new IllegalArgumentException("userUpdateRequest가 필요합니다.");
        }
        if (userUpdateRequest.newUsername() != null && userUpdateRequest.newUsername().trim()
            .isEmpty()) {
            throw new IllegalArgumentException("newUsername이 필요합니다.");
        }
        if (userUpdateRequest.newPassword() != null && userUpdateRequest.newPassword().trim()
            .isEmpty()) {
            throw new IllegalArgumentException("newPassword가 필요합니다.");
        }

        Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.empty();
        if (profile != null && !profile.isEmpty()) {
            profileCreateRequest = Optional.of(new BinaryContentCreateRequest(
                profile.getOriginalFilename(),
                profile.getContentType(),
                profile.getBytes()
            ));
        }
        User user = userService.update(userId, userUpdateRequest, profileCreateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId가 필요합니다.");
        }

        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<UserStatus> updateUserStatusByUserId(
        @PathVariable UUID userId,
        @RequestBody UserStatusUpdateRequest userStatusUpdateRequest
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("userId가 필요합니다.");
        }
        if (userStatusUpdateRequest == null) {
            throw new IllegalArgumentException("userStatusUpdateRequest가 필요합니다.");
        }

        UserStatus userStatus = userStatusService.updateByUserId(userId, userStatusUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(userStatus);
    }
}
