package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserRegisterRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateEmailRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdatePasswordRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateProfileImageRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateProfileSettingsRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateUsernameRequest;
import com.sprint.mission.discodeit.dto.response.FriendRequestResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.service.BasicFriendRequestService;
import com.sprint.mission.discodeit.service.BasicUserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/users")
public class UserController {

    private final BasicUserService userService;
    private final UserStatusService userStatusService;
    private final BasicFriendRequestService friendRequestService;

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> register(@RequestBody UserRegisterRequest body) {
        UserResponse created = userService.register(body);
        return ResponseEntity
                .created(URI.create("/api/users/" + created.id()))
                .body(created);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> findById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @RequestMapping(path = "/{id}/profile-settings", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateProfileSettings(@PathVariable("id") UUID id,
                                                      @RequestBody UserUpdateProfileSettingsRequest body) {
        userService.updateProfileSettings(id, body);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/{id}/profile-image", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateProfileImage(@PathVariable("id") UUID id,
                                                   @RequestBody UserUpdateProfileImageRequest body) {
        userService.updateProfileImage(id, body);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/{id}/email", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateEmail(@PathVariable("id") UUID id,
                                            @RequestBody UserUpdateEmailRequest body) {
        userService.updateEmail(id, body);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/{id}/username", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateUsername(@PathVariable("id") UUID id,
                                               @RequestBody UserUpdateUsernameRequest body) {
        userService.updateUsername(id, body);
        return ResponseEntity.noContent().build();
    }


    @RequestMapping(path = "/{id}/password", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updatePassword(@PathVariable("id") UUID id,
                                               @RequestBody UserUpdatePasswordRequest body) {
        userService.updatePassword(id, body);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        userService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/{id}/status", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateStatus(@PathVariable("id") UUID id,
                                             @RequestBody UserStatusUpdateRequest body) {
        userStatusService.updateByUserId(id, body);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/{id}/deactivate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deactivateAccount(@PathVariable("id") UUID id) {
        userService.deactivateAccount(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/{id}/friend-requests", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FriendRequestResponse>> getFriendRequests(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(friendRequestService.getFriendRequests(id));
    }

    @RequestMapping(path = "/{id}/friends/{friendId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> removeFriend(@PathVariable("id") UUID id,
                                             @PathVariable("friendId") UUID friendId) {
        userService.removeFriend(id, friendId);
        return ResponseEntity.noContent().build();
    }
}
