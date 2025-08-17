package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.global.api.ApiResponse;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // 사용자 등록
    @PostMapping
    public ResponseEntity<ApiResponse<UserCreateResponse>> create(@Valid @RequestBody UserCreateRequest request) {
        User created = userService.create(request, Optional.<BinaryContentCreateRequest>empty());

        // Response = Request 동일
        UserCreateResponse response = new UserCreateResponse(
                request.username(),
                request.email(),
                request.password()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    // 사용자 수정
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserUpdateResponse>> update(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest request) {
        User updated = userService.update(userId, request, Optional.<BinaryContentCreateRequest>empty());

        UserUpdateResponse response = new UserUpdateResponse(
                request.newUsername(),
                request.newEmail(),
                request.newPassword()
        );
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // 사용자 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    // 모든 사용자 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> list() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    // 사용자의 온라인 상태 업데이트
    @PatchMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<UserStatusUpdateResponse>> updateStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody UserStatusUpdateRequest request) {
        UserStatus updated = userStatusService.updateByUserId(userId, request);

        UserStatusUpdateResponse response = new UserStatusUpdateResponse(
                request.newLastActiveAt()
        );
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
