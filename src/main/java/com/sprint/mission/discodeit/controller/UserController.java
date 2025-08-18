package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
//* [ ] 사용자를 등록할 수 있다.
// * [ ] 사용자 정보를 수정할 수 있다.
// * [ ] 사용자를 삭제할 수 있다.
// * [ ] 모든 사용자를 조회할 수 있다.
// * [ ] 사용자의 온라인 상태를 업데이트할 수 있다.

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(path = "create",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 파일(multipart/form-data) 전달
    public ResponseEntity<User> createUser(
            @RequestPart UserCreateRequest userCreateRequest,
            // multipart 데이터에서 JSON 매핑 (유저 생성 정보)
            @RequestPart(required = false) MultipartFile profile
            // multipart 데이터에서 파일 부분을 매핑 (프로필 이미지, 선택사항)
            // JSON -> UserCreateRequest 객체, 파일 -> MultipartFile로 변환
    ) throws IOException {
        // 파일 처리 로직
        Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.empty();
        if(!profile.isEmpty()) {
            profileCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getSize(),
                    profile.getBytes()
            ));
        }
        User user = userService.create(userCreateRequest, profileCreateRequest);

        UserStatusCreateRequest statusCreateRequest = new UserStatusCreateRequest(user.getId(),user.getCreatedAt());
        userStatusService.create(statusCreateRequest);
        return ResponseEntity.status(201).body(user);
    }

    @RequestMapping(path = "update",
            method = RequestMethod.PATCH,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> update(@RequestParam UUID userId,
                                       @RequestPart UserUpdateRequest userUpdateRequest,
                                       @RequestPart(required = false) MultipartFile profile
    ) throws IOException {
        Optional<BinaryContentCreateRequest> profileUpdateRequest = Optional.empty();

        if (profile != null && !profile.isEmpty()) {
            profileUpdateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getSize(),
                    profile.getBytes()
            ));
        }
        User updatedUser = userService.update(userId, userUpdateRequest, profileUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @RequestMapping(path = "delete", method = RequestMethod.DELETE)
    public void delete(@RequestParam UUID userId){
        userService.delete(userId);
    }

    @RequestMapping(path = "findAll")
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }

    @RequestMapping(path = "update/{userId}", method=RequestMethod.PATCH)
    public ResponseEntity<UserStatus> updateUserStatus(
            @PathVariable("userId") UUID userId,
            @RequestBody UserStatusUpdateRequest userStatusUpdateRequest
    ) {
        UserStatus userStatus = userStatusService.updateByUserId(userId, userStatusUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(userStatus);
    }



}
