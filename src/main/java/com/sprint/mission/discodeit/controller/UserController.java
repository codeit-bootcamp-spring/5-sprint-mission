package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.UserDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // ✅ 생성자 주입
    @Autowired
    public UserController(UserService userService, UserStatusService userStatusService) {
        this.userService = userService;
        this.userStatusService = userStatusService;
    }

    // ✅ 사용자 등록
    @RequestMapping( // @RequestMapping 어노테이션은 하나하나 다 명시해줘야함
            value = "/create",
            method = RequestMethod.POST,
            consumes = "multipart/form-data") // 클라이언트가 보내는 데이터 타입
    public ResponseEntity<User> create(
            //따옴표 안 : 클라이언트가 보내는 form-data key의 필드명
            @RequestPart("userCreateRequest") UserCreateRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        User createdUser = userService.create(request, profileImage);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // ✅ 사용자 목록 조회
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> result = userService.findAll().stream()
                .map(this::convertToDto) // ✔️ 여기서 online 계산 포함
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // ✅ User → UserDto 변환 메서드
    private UserDto convertToDto(User user) {
        UserStatus status = user.getStatus(); // User 안에 UserStatus 포함되어 있어야 함
        Instant lastOnline = (status != null) ? status.getLastOnline() : null;
        boolean isOnline = lastOnline != null && Instant.now().minusSeconds(300).isBefore(lastOnline);

        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUserId(),       // username
                user.getEmail(),
                user.getProfileId(),    // profileId
                isOnline                // online
        );
    }

    // 사용자 단건 조회
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<UserResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    // 사용자 수정
    @RequestMapping(value = "/update",
            method = RequestMethod.PUT,
            consumes = "multipart/form-data"
    )
    public ResponseEntity<User> update(
            @RequestPart("userUpdateRequest") UserUpdateRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {
        User updatedUser = userService.update(request, profileImage);
        return ResponseEntity.ok(updatedUser); // 또는 ok().body(updatedUser)
    }

    // 사용자 삭제
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
