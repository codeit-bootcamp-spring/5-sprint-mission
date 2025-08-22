package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.api.ApiResult;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    //사용자 등록
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResult<UserDto>> create(@RequestPart("user") UserCreateRequest userCreateRequest,
                                                     @RequestPart(value = "profile", required = false) MultipartFile profile) throws IOException {
        Optional<BinaryContentCreateRequest> contentCreateRequest = Optional.empty();

        if (profile != null && !profile.isEmpty()) {
            contentCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }
        UserDto user = userService.create(userCreateRequest, contentCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.ok(user, "사용자가 생성되었습니다"));
    }

    //사용자 정보 수정
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResult<UserDto>> update(
            @PathVariable("id") UUID id,
            @RequestPart("user") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile) throws IOException {
        Optional<BinaryContentCreateRequest> contentCreateRequest = Optional.empty();

        if (profile != null && !profile.isEmpty()) {
            contentCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }

        UserDto updatedUser = userService.update(id, userUpdateRequest, contentCreateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(updatedUser, "수정 완료되었습니다"));
    }

    //사용자 삭제
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //모든 사용자 조회
    @GetMapping
    public ResponseEntity<ApiResult<List<UserDto>>> getAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(users));
    }

    //사용자 온라인 상태 업데이트
    @PatchMapping(value = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResult<UserStatusDto>> updateStatusUser(
            @PathVariable("id") UUID id,
            @RequestBody UserStatusUpdateRequest userStatusUpdateRequest) {
        UserStatusDto userStatus = userStatusService.updateByUserId(id, userStatusUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(userStatus, "사용자 " + id + " 의 상태가 변경되었습니다"));
    }
}
