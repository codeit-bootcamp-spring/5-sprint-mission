package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(path = "create",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // ResponseEntity -> HTTP 응답의 상태코드, 헤더, 바디를 세밀하게 제어할 때 사용함.
    public ResponseEntity<User> createUser( // 사용자 등록
            @RequestPart UserCreateRequest userCreateRequest,
            @RequestPart(required = false) MultipartFile profile
            )throws IOException {
        Optional<BinaryContentCreateRequest> binaryContentCreateRequest = Optional.empty();
        if(profile != null && !profile.isEmpty()){
            binaryContentCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }
        User user = userService.create(userCreateRequest, binaryContentCreateRequest);
        return ResponseEntity.ok(user);

    }

    @RequestMapping(path = "/update/{userId}", // 사용자 수정(415 에러발생)
            method =  RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> update(@PathVariable("userId") UUID userId,
                                       @RequestPart("request") UserUpdateRequest request,
                                       @RequestPart(required = false) MultipartFile file) throws IOException {

        // 변환해서 BinaryContentCreateRequest로 만들기
        Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.empty();
        if (!file.isEmpty()) {
            profileCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            ));
        }
        User userUpdate = userService.update(userId, request, profileCreateRequest);
        return ResponseEntity.ok(userUpdate);
    }

    @RequestMapping(path = "/delete", method = RequestMethod.DELETE) // 사용자 제거
    public ResponseEntity<User> delete(@RequestParam UUID userId){
        User user = userService.find(userId);
        userService.delete(userId);
        return ResponseEntity.ok(user);
    }

    @RequestMapping(path = "/findAll") // 모든 사용자 조회
    public ResponseEntity<List<User>> findAll(){
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @RequestMapping(
            path = "/update-status/{userId}",
            method = RequestMethod.PUT
    )
    // 온라인 업데이트 (415 에러)
    public ResponseEntity<UserStatus> updateUserStatus(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest request
    ) {
        UserStatus userStatus = userStatusService.update(userId, request);
        return ResponseEntity.ok(userStatus);
    }
}
