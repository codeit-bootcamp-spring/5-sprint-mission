package com.sprint.mission.discodeit.Controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // 사용자 등록
    @RequestMapping(value = "/create", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> create(
            @RequestPart UserCreateRequest userCreateRequest,
            @RequestPart (required = false) MultipartFile profile
            ) throws IOException {
        Optional<BinaryContentCreateRequest> binaryContentCreateRequest = Optional.empty();
        if (profile != null) {
            binaryContentCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }
        User created = userService.create(userCreateRequest, binaryContentCreateRequest);
        return ResponseEntity.ok(created);
    }

    // 사용자 수정
    @RequestMapping(value = "/update/{userId}", method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> update(
            @PathVariable UUID userId,
            @RequestPart UserUpdateRequest userUpdateRequest,
            @RequestPart(required = false) MultipartFile profile
            ) throws IOException {
        Optional<BinaryContentCreateRequest> binaryContentCreateRequest = Optional.empty();
        if (profile != null) {
            binaryContentCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }

        User updated = userService.update(userId, userUpdateRequest, binaryContentCreateRequest);
        return ResponseEntity.ok(updated);
    }

    // 사용자 삭제
    @RequestMapping(value = "/delete/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    // 사용자 조회
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    // 사용자 온라인 상태 업데이트
    @RequestMapping(value = "/status/{userId}", method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserStatus> updateUserStatus(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest userStatusUpdateRequest
    ) {
        UserStatus userStatus = userStatusService.updateByUserId(userId, userStatusUpdateRequest);
        return ResponseEntity.ok(userStatus);
    }
}
