package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserCreateResponse;
import com.sprint.mission.discodeit.dto.response.UserUpdateResponse;
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
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;


    //    [ ] 사용자를 등록할 수 있다.
    @RequestMapping(path = "create",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserCreateResponse> createUser( //매개변수들, 반환값은 응답본문과 user객체
                                                          @RequestPart UserCreateRequest userCreateRequest,   //요청본문에서 데이터를 받겠다
                                                          @RequestPart(required = false) MultipartFile profile    //요청본문에서 이미지를받겠다,필수아님
    ) throws IOException {
        //Optional은 값이 있을 수도 있고 없을 수도 있는 경우
        Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.empty();
        if (profile!=null) {    //이미지가 있으면
            profileCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }
        User user = userService.create(userCreateRequest, profileCreateRequest);
        UserCreateResponse userCreateResponse = new UserCreateResponse(user.getId(), user.getUsername(), user.getEmail(), user.getCreatedAt(), user.getProfileId());
        //HTTP 201 Created 상태 코드와 함께 생성된 ResponseDTO 객체를 반환합니다.
        return ResponseEntity.status(201).body(userCreateResponse);
    }

    //            [ ] 모든 사용자를 조회할 수 있다.
    @RequestMapping(path = "findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity
                .status(HttpStatus.OK) // HTTP 200 OK 상태 코드와 함께 users 리스트를 반환
                .body(users);
    }

//            [ ] 사용자 정보를 수정할 수 있다

    @RequestMapping(path = "update/{userId}",
            method = RequestMethod.PUT)
    public ResponseEntity<UserUpdateResponse> updateUser(
            @RequestPart UserUpdateRequest userUpdateRequest,
            @PathVariable UUID userId,
            @RequestPart(required = false) MultipartFile profile
    ) throws IOException {
        Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.empty();
        if (profile!=null) {
            profileCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }
        User user = userService.update(userId, userUpdateRequest, profileCreateRequest);
        UserUpdateResponse userUpdateResponse = new UserUpdateResponse(user.getId(), user.getUsername(), user.getEmail(), user.getCreatedAt(), user.getUpdatedAt(), user.getProfileId());
        return ResponseEntity.status(201).body(userUpdateResponse);
    }


    //[ ] 사용자를 삭제할 수 있다.
    @RequestMapping(path = "delete/{userId}",
            method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        userService.delete(userId);

        return ResponseEntity.status(204).build();

    }

//[ ] 사용자의 온라인 상태를 업데이트할 수 있다.

    @RequestMapping(path = "updateUserStatus/{userId}",
            method = RequestMethod.PUT)
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable UUID userId,
            @RequestPart UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusService.updateByUserId(userId, request);
        return ResponseEntity.status(204).build();
    }

}
