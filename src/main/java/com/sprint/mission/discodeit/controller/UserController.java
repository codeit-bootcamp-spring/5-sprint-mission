package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;
    private final ChannelService channelService;
    private final ReadStatusService readStatusService;

    public UserController(
            @Qualifier("basicUserService") UserService userService,
            @Qualifier("basicUserStatusService") UserStatusService userStatusService,
            @Qualifier("basicChannelService") ChannelService channelService,
            @Qualifier("basicReadStatusService") ReadStatusService readStatusService
    ) {
        this.userService = userService;
        this.userStatusService = userStatusService;
        this.channelService = channelService;
        this.readStatusService = readStatusService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request) {
        UserResponse response = userService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID userId) {
        UserResponse user = userService.find(userId);
        return ResponseEntity.ok(user);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID userId, @RequestBody UserUpdateRequest request) {
        if (request.getId() == null || !userId.equals(request.getId())) {
            throw new IllegalArgumentException("User ID in path does not match ID in request body.");
        }
        UserResponse updatedUser = userService.update(request);
        return ResponseEntity.ok(updatedUser);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{userId}/status", method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateUserStatus(@PathVariable UUID userId, @RequestBody UserStatusUpdateByUserIdRequest request) {
        userStatusService.updateUserStatusByUserId(userId, request);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{userId}/channels", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelResponse>> getChannelsForUser(@PathVariable UUID userId) {
        List<ChannelResponse> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(channels);
    }

    @RequestMapping(value = "/{userId}/read-statuses", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusResponse>> getReadStatusesForUser(@PathVariable UUID userId) {
        List<ReadStatusResponse> readStatuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(readStatuses);
    }
}
