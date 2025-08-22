package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binaryContent.FileDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;
    private final BinaryContentService binaryContentService;

    // 사용자 등록: POST /users
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<UserResponse.detail> create(
            @RequestPart("userCreateRequest") UserRequest.Create req,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {

        UserDto userDto = new UserDto(req.username(), req.email(), req.password());
        FileDto fileDto = null;

        if(profile != null) {
            if(profile.isEmpty()) throw new IllegalArgumentException("이미지 파일이 비어있습니다.");
            fileDto = new FileDto(UUID.randomUUID().toString(), profile.getContentType(), profile.getBytes());
        }

        User user = userService.create(userDto, fileDto);
        boolean online = userStatusService.isOnline(user.getId());

        return ResponseEntity.ok(UserResponse.detail.from(user, online));
    }

    // 사용자 정보 수정 // 이름, 이메일
    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse.detail> update(
            @PathVariable UUID userId,
            @RequestPart("userUpdateRequest") UserRequest.Update req,
            @RequestPart(value = "profile", required = false) MultipartFile profile) throws IOException {
        FileDto fileDto = null;

        if(profile != null) {
            if(profile.isEmpty()) throw new IllegalArgumentException("이미지 파일이 비어있습니다.");

            fileDto = new FileDto(UUID.randomUUID().toString(), profile.getContentType(), profile.getBytes());
        }

        User user = userService.update(userId, req.newUsername(), fileDto);
        boolean online = userStatusService.isOnline(user.getId());

        return ResponseEntity.ok(UserResponse.detail.from(user, online));
    }

    // 비밀번호 수정
    @PatchMapping
    public ResponseEntity<String> updatePassword(@Valid @ModelAttribute UserRequest.passwordReset req) {
        userService.updatePassword(req.userId(), req.password(), req.newPassword());
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    // 모든 사용자 조회: GET /user
    @GetMapping
    public ResponseEntity<List<UserResponse.summary>> findAll() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users.stream()
                .map(user -> UserResponse.summary.from(user, userStatusService.isOnline(user.getId()))
        ).toList());
    }

    // ID로 사용자 조회: GET /user/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse.detail> findById(@PathVariable UUID id) {

        User user = userService.findById(id);
        boolean online = userStatusService.isOnline(user.getId());

        return ResponseEntity.ok(UserResponse.detail.from(user, online));
    }

    // 이메일로 사용자 조회
    @GetMapping(params = "email")
    public ResponseEntity<UserResponse.detail> findByEmail(@RequestParam String email) {

        User user = userService.findByEmail(email);
        boolean online = userStatusService.isOnline(user.getId());

        return ResponseEntity.ok(UserResponse.detail.from(user, online));
    }

    // 삭제: DELETE /user/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.ok("계정이 삭제되었습니다.");
    }
}
