package com.codeit.mission.discodeit.controller;

import com.codeit.mission.discodeit.controller.api.UserApi;
import com.codeit.mission.discodeit.dto.data.UserDto;
import com.codeit.mission.discodeit.dto.data.UserStatusDto;
import com.codeit.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.codeit.mission.discodeit.dto.request.UserCreateRequest;
import com.codeit.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.codeit.mission.discodeit.dto.request.UserUpdateRequest;
import com.codeit.mission.discodeit.service.UserService;
import com.codeit.mission.discodeit.service.UserStatusService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController implements UserApi {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Override
    public ResponseEntity<UserDto> create(
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        log.info("사용자 생성 API 호출 - username: {}, email: {}, hasProfile: {}",
                userCreateRequest.username(), userCreateRequest.email(),
                profile != null && !profile.isEmpty());

        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);
        UserDto createdUser = userService.create(userCreateRequest, profileRequest);

        log.info("사용자 생성 API 성공 - userId: {}, username: {}",
                createdUser.id(), userCreateRequest.username());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    @PatchMapping(
            path = "{userId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    @Override
    public ResponseEntity<UserDto> update(
            @PathVariable("userId") UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        log.info("사용자 수정 API 호출 - userId: {}, newUsername: {}, newEmail: {}, hasNewProfile: {}",
                userId, userUpdateRequest.newUsername(), userUpdateRequest.newEmail(),
                profile != null && !profile.isEmpty());

        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);
        UserDto updatedUser = userService.update(userId, userUpdateRequest, profileRequest);

        log.info("사용자 수정 API 성공 - userId: {}, newUsername: {}",
                userId, userUpdateRequest.newUsername());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUser);
    }

    @DeleteMapping(path = "{userId}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable("userId") UUID userId) {
        log.info("사용자 삭제 API 호출 - userId: {}", userId);
        userService.delete(userId);
        log.info("사용자 삭제 API 성공 - userId: {}", userId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    @Override
    public ResponseEntity<List<UserDto>> findAll() {
        log.debug("전체 사용자 조회 API 호출");
        List<UserDto> users = userService.findAll();
        log.info("전체 사용자 조회 API 성공 - 조회된 사용자 수: {}", users.size());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }

    @PatchMapping(path = "{userId}/userStatus")
    @Override
    public ResponseEntity<UserStatusDto> updateUserStatusByUserId(
            @PathVariable("userId") UUID userId,
            @RequestBody UserStatusUpdateRequest request) {
        log.info("사용자 상태 업데이트 API 호출 - userId: {}", userId);
        UserStatusDto updatedUserStatus = userStatusService.updateByUserId(userId, request);
        log.info("사용자 상태 업데이트 API 성공 - userId: {}", userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUserStatus);
    }

    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
        if (profileFile.isEmpty()) {
            return Optional.empty();
        } else {
            try {
                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                        profileFile.getOriginalFilename(),
                        profileFile.getContentType(),
                        profileFile.getBytes()
                );
                return Optional.of(binaryContentCreateRequest);
            } catch (IOException e) {
                log.error("프로필 파일 처리 중 오류 발생 - fileName: {}, 오류: {}",
                        profileFile.getOriginalFilename(), e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }
}
