package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
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

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public ResponseEntity<User> createUser(
            @RequestPart UserCreateRequest userCreateRequest,
            @RequestPart(required = false) MultipartFile profile) throws IOException {

        Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.empty();
        if (!profile.isEmpty()) {
            profileCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }

        User user = userService.create(userCreateRequest, profileCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @RequestMapping(path = "/update/{userId}", method = RequestMethod.PUT)
    public ResponseEntity<User> updateUser(
            @PathVariable UUID userId,
            @RequestPart UserUpdateRequest userUpdateRequest,
            @RequestPart(required = false) MultipartFile profile) throws IOException {

        Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.empty();
        if (!profile.isEmpty()) {
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
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @RequestMapping(value = "/updateStatus/{userId}", method = RequestMethod.PATCH)
    public ResponseEntity<UserStatus> updateStatus(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest userStatusUpdateRequest) {

        UserStatus userStatus = userStatusService.updateByUserId(userId, userStatusUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(userStatus);
    }
}
