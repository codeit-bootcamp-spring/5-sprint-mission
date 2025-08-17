package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user") // 필요하면 /api/users 로 변경
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // 생성 (항상 multipart)
    @RequestMapping(
            path = "create",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserDto> createUser(
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {

        Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.empty();
        if (profile != null && !profile.isEmpty()) {
            profileCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }

        User created = userService.create(userCreateRequest, profileCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.find(created.getId()));
    }

    // 단일 조회
    @RequestMapping(path = "find/{userId}", method = RequestMethod.GET)
    public ResponseEntity<UserDto> findUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.find(userId));
    }

    // 전체 조회
    @RequestMapping(path = "findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    // 수정 (항상 multipart) - 프로필 없으면 profile 파트 생략 가능
    @RequestMapping(
            path = "update/{userId}",
            method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserDto> update(
            @PathVariable("userId") UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest req,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {

        Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.empty();
        if (profile != null && !profile.isEmpty()) {
            profileCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }

        userService.update(userId, req, profileCreateRequest);
        return ResponseEntity.ok(userService.find(userId));
    }

    // 삭제
    @RequestMapping(path = "delete/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    // 온라인 상태 업데이트
    @RequestMapping(
            path = "/{userId}/status",
            method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> updateStatus(@PathVariable UUID userId,
                                             @RequestBody UserStatusUpdateRequest req) {
        userStatusService.updateByUserId(userId, req);
        return ResponseEntity.noContent().build();
    }
}