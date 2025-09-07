package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.dto.response.UserStatusResponseDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDto> create(
            @RequestPart("user") UserCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {

        Optional<BinaryContentCreateRequest> optionalProfile = Optional.empty();

        if (profile != null && !profile.isEmpty()) {
            optionalProfile = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }

        UserResponseDto response = userService.create(request, optionalProfile);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDto> update(
            @PathVariable UUID id,
            @RequestPart("user") UserUpdateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {

        Optional<BinaryContentCreateRequest> optionalProfile = Optional.empty();

        if (profile != null && !profile.isEmpty()) {
            optionalProfile = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }
        UserResponseDto response = userService.update(id, request, optionalProfile);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<UserStatusResponseDto> updateStatus(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(userStatusService.update(userId, request));
    }
}

