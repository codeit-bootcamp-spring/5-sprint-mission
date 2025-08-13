package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserRegisterRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateEmailRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdatePasswordRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateProfileImageRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateProfileSettingsRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateUsernameRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterRequest body) {
        UserResponse res = userService.register(body);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(res.id()).toUri();
        return ResponseEntity.created(location).body(res);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(userService.find(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PatchMapping(path = "/{id}/profile-settings")
    public ResponseEntity<Void> updateProfileSettings(@PathVariable("id") UUID id,
                                                      @Valid @RequestBody UserUpdateProfileSettingsRequest body) {
        userService.updateProfileSettings(id, body);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{id}/profile-image")
    public ResponseEntity<Void> updateProfileImage(@PathVariable("id") UUID id,
                                                   @Valid @RequestBody UserUpdateProfileImageRequest body) {
        userService.updateProfileImage(id, body);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{id}/email")
    public ResponseEntity<Void> updateEmail(@PathVariable("id") UUID id,
                                            @Valid @RequestBody UserUpdateEmailRequest body) {
        userService.updateEmail(id, body);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{id}/username")
    public ResponseEntity<Void> updateUsername(@PathVariable("id") UUID id,
                                               @Valid @RequestBody UserUpdateUsernameRequest body) {
        userService.updateUsername(id, body);
        return ResponseEntity.noContent().build();
    }


    @PutMapping(path = "/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable("id") UUID id,
                                               @Valid @RequestBody UserUpdatePasswordRequest body) {
        userService.updatePassword(id, body);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        userService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable("id") UUID id,
                                             @Valid @RequestBody UserStatusUpdateRequest body) {
        userStatusService.updateStatusByUserId(id, body);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{id}/deactivation")
    public ResponseEntity<Void> deactivateAccount(@PathVariable("id") UUID id) {
        userService.deactivateAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{id}/friends")
    public ResponseEntity<List<UserResponse>> getFriends(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(userService.getFriends(id));
    }

    @DeleteMapping(path = "/{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable("id") UUID id,
                                             @PathVariable("friendId") UUID friendId) {
        userService.removeFriend(id, friendId);
        return ResponseEntity.noContent().build();
    }
}
