package com.sprint.mission.discodeit.user.presentation;

import com.sprint.mission.discodeit.user.application.UserService;
import com.sprint.mission.discodeit.user.presentation.dto.UserCreateRequest;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import com.sprint.mission.discodeit.user.presentation.dto.UserUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;
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

import static com.sprint.mission.discodeit.global.util.RequestExtractor.extractIpAddress;
import static com.sprint.mission.discodeit.global.util.RequestExtractor.extractUserAgent;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

    private final UserService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(
        @RequestPart("userCreateRequest") @Valid UserCreateRequest request,
        @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        return userService.create(request, profile);
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @PatchMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserDto update(
        @PathVariable UUID userId,
        @RequestPart(value = "userUpdateRequest") @Valid UserUpdateRequest request,
        @RequestPart(value = "profile", required = false) MultipartFile profile,
        HttpServletRequest servletRequest
    ) {
        String ipAddress = extractIpAddress(servletRequest);
        String userAgent = extractUserAgent(servletRequest);

        return userService.update(userId, request, profile, ipAddress, userAgent);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable UUID userId) {
        userService.deleteById(userId);
    }
}
