package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @RequestMapping(path = "create",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDto> createUser(
            @ModelAttribute UserCreateRequest userCreateRequest,
            @RequestPart(required = false) MultipartFile profile
    ) throws IOException {
        Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.empty();
        if (profile != null && !profile.isEmpty()) {
            profileCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getBytes(),
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getSize()
            ));
        }

        UserResponseDto createdUser = userService.create(userCreateRequest, profileCreateRequest.orElse(null));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @RequestMapping(path = "findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll().stream()
                .map(u -> new UserDto(
                        u.id(),
                        u.createdAt(),  // 여기서 createdAt, updatedAt 필요 → UserResponseDto 수정하거나 UserDto로 변환 시 추가
                        u.updatedAt(),
                        u.username(),
                        u.email(),
                        u.profileImageId(),
                        u.online()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    @RequestMapping(path = "find/{id}", method = RequestMethod.GET)
    public ResponseEntity<UserResponseDto> findById(@PathVariable("id") UUID userId) {
        UserResponseDto user = userService.find(userId);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @RequestMapping(path = "update", method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDto> updateUser(
            @ModelAttribute UserUpdateRequest userUpdateRequest,
            @RequestPart(required = false) MultipartFile profile
    ) throws IOException {
        Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.empty();
        if (profile != null && !profile.isEmpty()) {
            profileCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getBytes(),
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getSize()
                        ));
        }
        UserUpdateRequest requestWithProfile = profileCreateRequest
                .map(profileReq -> new UserUpdateRequest(
                        userUpdateRequest.id(),
                        userUpdateRequest.username(),
                        userUpdateRequest.email(),
                        userUpdateRequest.password(),
                        profileReq
                ))
                .orElse(userUpdateRequest);
        UserResponseDto updatedUser = userService.update(requestWithProfile);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @RequestMapping(path = "delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable("id") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(path = "/{userId}/lastAccess", method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateLastAccess(@PathVariable UUID userId) {
        userService.updateLastAccessAt(userId, Instant.now());
        return ResponseEntity.noContent().build();
    }
}
