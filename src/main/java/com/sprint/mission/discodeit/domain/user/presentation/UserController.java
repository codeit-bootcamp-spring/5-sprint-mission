package com.sprint.mission.discodeit.domain.user.presentation;

import com.sprint.mission.discodeit.domain.user.application.UserService;
import com.sprint.mission.discodeit.domain.user.presentation.dto.UserCreateRequest;
import com.sprint.mission.discodeit.domain.user.presentation.dto.UserDto;
import com.sprint.mission.discodeit.domain.user.presentation.dto.UserUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

    private final UserService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(
        @RequestPart("userCreateRequest") @Valid UserCreateRequest req,
        @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        return userService.create(req, profile);
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @PatchMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserDto update(
        @PathVariable UUID userId,
        @RequestPart(value = "userUpdateRequest") @Valid UserUpdateRequest req,
        @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        return userService.update(userId, req, profile);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable UUID userId) {
        userService.deleteById(userId);
    }
}
