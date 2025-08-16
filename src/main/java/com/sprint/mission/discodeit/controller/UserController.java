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
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    /**
     * [POST] 사용자 등록 (심화 요구사항과 무관: 기존 그대로 유지)
     * - multipart/form-data
     * - @RequestPart("userCreateRequest") 에 JSON 문자열(또는 JSON Content-Type) 전달
     * - @RequestPart(value="profile") 로 프로필 이미지(선택)
     */
    @RequestMapping(path = "create", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> create(
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
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * [PUT] 사용자 정보 수정 (선택: 프로필 교체 가능)
     * - multipart/form-data
     * - @PathVariable userId
     * - @RequestPart("userUpdateRequest") JSON
     * - @RequestPart(value="profile") 파일(옵션)
     */
    @RequestMapping(path = "update/{userId}", method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> update(
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
        User updated = userService.update(userId, req, profileCreateRequest);
        return ResponseEntity.ok(updated);
    }

    /**
     * [DELETE] 사용자 삭제
     */
    @RequestMapping(path = "delete/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("userId") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * [GET] 사용자 목록 조회 (심화 요구사항)
     * - URL: /api/user/findAll
     * - 파라미터/바디 없음
     * - 응답: ResponseEntity<List<UserDto>>
     *   record UserDto(UUID id, Instant createdAt, Instant updatedAt, String username,
     *                  String email, UUID profileId, Boolean online)
     */
    @RequestMapping(path = "findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * [PATCH] 사용자의 온라인 상태 업데이트
     * - URL: /api/user/status/update/{userId}
     * - Body: UserStatusUpdateRequest
     * - 응답: 204 No Content
     */
    @RequestMapping(path = "status/update/{userId}", method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateStatus(
            @PathVariable("userId") UUID userId,
            @RequestBody UserStatusUpdateRequest req
    ) {
        userStatusService.updateByUserId(userId, req);
        return ResponseEntity.noContent().build();
    }
}
