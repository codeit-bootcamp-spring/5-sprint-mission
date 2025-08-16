package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.*;
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
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // [POST] 사용자 등록 (multipart: userCreateRequest JSON + profile 파일)
    @RequestMapping(path = "create", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> create(
            @RequestPart("userCreateRequest") UserCreateRequest req,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {
        Optional<BinaryContentCreateRequest> profileReq = Optional.empty();
        if (profile != null && !profile.isEmpty()) {
            profileReq = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }
        User created = userService.create(req, profileReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // [PUT] 사용자 정보 수정 (multipart: userUpdateRequest JSON + optional profile)
    @RequestMapping(path = "update/{userId}", method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> update(
            @PathVariable("userId") UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest req,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {
        Optional<BinaryContentCreateRequest> profileReq = Optional.empty();
        if (profile != null && !profile.isEmpty()) {
            profileReq = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }
        User updated = userService.update(userId, req, profileReq);
        return ResponseEntity.ok(updated);
    }

    // [DELETE] 사용자 삭제
    @RequestMapping(path = "delete/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("userId") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    // [GET] 모든 사용자 조회
    @RequestMapping(path = "findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    // [PATCH] 사용자의 온라인 상태 업데이트 (마지막 접속 시각 등)
    @RequestMapping(path = "status/update/{userId}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateStatus(
            @PathVariable("userId") UUID userId,
            @RequestBody UserStatusUpdateRequest req
    ) {
        userStatusService.updateByUserId(userId, req);
        return ResponseEntity.noContent().build();
    }
}
