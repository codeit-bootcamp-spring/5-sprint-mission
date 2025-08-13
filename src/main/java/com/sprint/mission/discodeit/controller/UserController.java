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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(path="/create", method=RequestMethod.POST, consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> createUser(
            @RequestPart UserCreateRequest userRequest,
            @RequestPart(required = false) MultipartFile profile) throws IOException {
        Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.empty();
        if(!profile.isEmpty()) {
            profileCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }
        User savedUser = userService.create(userRequest, profileCreateRequest);
        return ResponseEntity.status(201).body(savedUser);
    }

    @RequestMapping(path="/{id}/update",
            method=RequestMethod.POST,
            consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> updateUser(
            @PathVariable UUID id,
            @RequestPart UserUpdateRequest userRequest,
            @RequestPart(required = false) MultipartFile profile) throws IOException {
        Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.empty();
        if(!profile.isEmpty()) {
            profileCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }

        User savedUser = userService.update(id, userRequest, profileCreateRequest);
        return ResponseEntity.status(201).body(savedUser);
    }

    @RequestMapping(path = "findAll")
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }

    @RequestMapping(path="/{id}/delete", method=RequestMethod.DELETE)
    public ResponseEntity<UserDto> deleteUserById(@PathVariable UUID id) {
        Optional<UserDto> user = Optional.ofNullable(userService.find(id));
//        user.orElseThrow(() -> new NoSuchElementException("User with id " + id + " does not exist"));
        userService.delete(id);
        return ResponseEntity.ok(user.get());
    }

    @RequestMapping(path="/{id}/updateOnline", method=RequestMethod.POST)
    public ResponseEntity<UserStatus> updateOnlineStatus(@PathVariable UUID id, @RequestBody UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusService.updateByUserId(id, request);
        return ResponseEntity.status(201).body(userStatus);
    }
}

