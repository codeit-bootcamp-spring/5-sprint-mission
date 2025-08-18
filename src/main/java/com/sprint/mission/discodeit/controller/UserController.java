package com.sprint.mission.discodeit.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@ResponseBody
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    //등록
    @RequestMapping(path = "create",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<User> create(@RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
                                       @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        //MultipartFile-> BinaryContentCreateRequest 으로 변경
        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);
        User createUser = userService.create(userCreateRequest, profileRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createUser);
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
                throw new RuntimeException(e);
            }
        }
    }

    //수정
    @RequestMapping(path = "update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> update(
            @RequestParam("userId") UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);
        User updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    //삭제
    @RequestMapping(path = "delete")
    public ResponseEntity<Void> deleteUser(@RequestParam("userId") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //조회
    @RequestMapping(path = "findAll")
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }
    //사용자 온라인 상태 업데이트
    @RequestMapping(path = "updateUserStatusByUserId")
    public ResponseEntity<UserStatus> updateUserStatusByUserId(
            @RequestParam("userId")UUID userId,
            @RequestBody UserStatusUpdateRequest request){
        UserStatus updatedUserStatus = userStatusService.update(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUserStatus);
    }


}
