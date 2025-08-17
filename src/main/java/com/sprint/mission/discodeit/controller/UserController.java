package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    public UserController(UserService userService, UserStatusService userStatusService) {
        this.userService = userService;
        this.userStatusService = userStatusService;
    }

    @RequestMapping(
            path = "create",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<User> createUser(
            @RequestPart UserCreateRequest userCreateRequest,
            @RequestPart(required = false) MultipartFile profile
    ) throws IOException {
        Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.empty();
        if (!profile.isEmpty()) {
            BinaryContent.ContentType contentType = mapToContentType(profile.getContentType());
            profileCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    contentType,
                    profile.getBytes()
            ));
        }
        User user = userService.create(userCreateRequest, profileCreateRequest);

        return ResponseEntity.status(201).body(user);
    }

    private static BinaryContent.ContentType mapToContentType(String contentType) {
        if (contentType == null) {
            return BinaryContent.ContentType.TEXT;
        }
        if (contentType.startsWith("image/")) {
            return BinaryContent.ContentType.IMAGE;
        }
        if (contentType.startsWith("video/")) {
            return BinaryContent.ContentType.VIDEO;
        }
        return BinaryContent.ContentType.TEXT;
    }

    @RequestMapping(path = "find", method = RequestMethod.GET)
    public ResponseEntity<UserDto> find(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.find(userId));
    }

    @RequestMapping(path = "findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }

    @RequestMapping(
            method = RequestMethod.PUT, value = "/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<User> update(@PathVariable UUID userId,
                                       @RequestBody UserUpdateRequest req) {
        if (req == null) throw new IllegalArgumentException("Empty");
        User updated = userService.update(userId, req, Optional.empty());
        return ResponseEntity.ok(updated);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{userId}")
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(
            method = RequestMethod.PATCH, value = "/{userId}/status",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserStatus> updateStatus(@PathVariable UUID userId,
                                                   @RequestBody UserStatusUpdateRequest req) {
        if (req == null) throw new IllegalArgumentException("Empty");
        return ResponseEntity.ok(userStatusService.updateByUserId(userId, req));
    }
}
