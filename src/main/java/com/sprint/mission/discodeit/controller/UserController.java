package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // TODO 메서드 2개로 분리: POST /users, POST /users/{id}/profile
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> create(
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile) {

        BinaryContentCreateRequest binaryContentCreateRequest = resolveProfileRequest(profile);
        User user = userService.create(userCreateRequest, binaryContentCreateRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    @RequestMapping(path = "{userId}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> update(
            @PathVariable("userId") UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile) {

        BinaryContentCreateRequest binaryContentCreateRequest = resolveProfileRequest(profile);
        User updatedUser = userService.update(userId, userUpdateRequest, binaryContentCreateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @RequestMapping(path = "{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("userId") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @RequestMapping(path = "{userId}/user-status", method = RequestMethod.PATCH)
    public ResponseEntity<UserStatus> updateUserStatusByUserId(
            @PathVariable("userId") UUID userId,
            @RequestBody UserStatusUpdateRequest userStatusUpdateRequest) {

        UserStatus userStatus = userStatusService.updateByUserId(userId, userStatusUpdateRequest);
        return ResponseEntity.ok(userStatus);
    }

    private BinaryContentCreateRequest resolveProfileRequest(MultipartFile profile) {
        if (profile == null) {
            return null;
        }

        try {
            String filename = profile.getOriginalFilename();
            String contentType = profile.getContentType();
            byte[] bytes = profile.getBytes();

            return new BinaryContentCreateRequest(filename, contentType, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
