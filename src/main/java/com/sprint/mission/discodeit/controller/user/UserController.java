package com.sprint.mission.discodeit.controller.user;

import com.sprint.mission.discodeit.dto.request.status.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserRegisterRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateEmailRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdatePasswordRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdatePhoneNumberRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateProfileImageRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateProfileSettingsRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateUsernameRequest;
import com.sprint.mission.discodeit.dto.response.user.UserRegisterResponse;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.service.user.UserService;
import com.sprint.mission.discodeit.service.userstatus.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegisterResponse register(@Valid @RequestBody UserRegisterRequest body) {
        return userService.register(body);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse find(@PathVariable("id") UUID id) {
        return userService.find(id);
    }

    @GetMapping({"", "/"})
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> findAll(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {
        if (username != null && email != null) {
            throw new IllegalArgumentException("username과 email은 동시에 포함될 수 없습니다.");
        }
        if (username != null) {
            return userService.findByUsername(username);
        }
        if (email != null) {
            return userService.findByEmail(email);
        }
        return userService.findAll();
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") UUID id) {
        userService.deleteAccount(id);
    }

    @PatchMapping(path = "/{id}/profile-settings")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProfileSettings(@PathVariable("id") UUID id,
                                      @Valid @RequestBody UserUpdateProfileSettingsRequest body) {
        userService.updateProfileSettings(id, body);
    }

    @PutMapping(path = "/{id}/profile-image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProfileImage(@PathVariable("id") UUID id,
                                   @Valid @RequestBody UserUpdateProfileImageRequest body) {
        userService.updateProfileImage(id, body);
    }

    @DeleteMapping(path = "/{id}/profile-image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearProfileImage(@PathVariable("id") UUID id) {
        userService.clearProfileImage(id);
    }

    @PutMapping(path = "/{id}/email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEmail(@PathVariable("id") UUID id,
                            @Valid @RequestBody UserUpdateEmailRequest body) {
        userService.updateEmail(id, body);
    }

    @PutMapping(path = "/{id}/username")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUsername(@PathVariable("id") UUID id,
                               @Valid @RequestBody UserUpdateUsernameRequest body) {
        userService.updateUsername(id, body);
    }

    @PutMapping(path = "/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(@PathVariable("id") UUID id,
                               @Valid @RequestBody UserUpdatePasswordRequest body) {
        userService.updatePassword(id, body);
    }

    @PutMapping(path = "/{id}/phone-number")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePhoneNumber(@PathVariable("id") UUID id,
                                  @Valid @RequestBody UserUpdatePhoneNumberRequest body) {
        userService.updatePhoneNumber(id, body);
    }

    @DeleteMapping(path = "/{id}/phone-number")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearPhoneNumber(@PathVariable("id") UUID id) {
        userService.clearPhoneNumber(id);
    }

    @PutMapping(path = "/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(@PathVariable("id") UUID id,
                             @Valid @RequestBody UserStatusUpdateRequest body) {
        userStatusService.updateStatusByUserId(id, body);
    }

    @PostMapping(path = "/{id}/deactivation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateAccount(@PathVariable("id") UUID id) {
        userService.deactivateAccount(id);
    }

    @GetMapping(path = "/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getFriends(@PathVariable("id") UUID id) {
        return userService.getFriends(id);
    }

    @DeleteMapping(path = "/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable("id") UUID id,
                             @PathVariable("friendId") UUID friendId) {
        userService.removeFriend(id, friendId);
    }
}
