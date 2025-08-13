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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
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

    @RequestMapping(value = "/update/{userId}", method = RequestMethod.PUT)
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
        if (userUpdateRequest.newUsername() != null && userUpdateRequest.newUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("newUsername이 필요합니다.");
        }
        if (userUpdateRequest.newPassword() != null && userUpdateRequest.newPassword().trim().isEmpty()) {
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

    @RequestMapping(value = "/delete/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId가 필요합니다.");
        }

        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @RequestMapping(value = "/{userId}/status", method = RequestMethod.PATCH)
    public ResponseEntity<UserStatus> updateOnlineStatus(
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
