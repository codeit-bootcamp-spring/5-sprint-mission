package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(
        @RequestPart("userCreateRequest")
        @Valid
        UserCreateRequest req,

        @RequestPart(value = "profile", required = false)
        MultipartFile profile
    ) {
        return userService.create(req, profile);
    }

    @DeleteMapping(path = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("userId") UUID userId) {
        userService.delete(userId);
    }

    @PatchMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserDto update(
        @PathVariable("userId")
        UUID userId,

        @RequestPart(value = "userUpdateRequest")
        @Valid
        UserUpdateRequest req,

        @RequestPart(value = "profile", required = false)
        MultipartFile profile
    ) {
        return userService.update(userId, req, profile);
    }

    @PatchMapping(path = "/{userId}/userStatus")
    @ResponseStatus(HttpStatus.OK)
    public UserStatusDto updateUserStatusByUserId(
        @PathVariable("userId")
        UUID userId,

        @RequestBody
        UserStatusUpdateRequest req
    ) {
        return userService.updateUserStatusByUserId(userId, req);
    }
}
