package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.api.ApiResult;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
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
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/register", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResult<User>> userRegister(@RequestPart("user") UserCreateRequest userCreateRequest,
                                                        @RequestPart(value = "profile", required = false) MultipartFile profile) throws IOException {
        Optional<BinaryContentCreateRequest> contentCreateRequest = Optional.empty();

        if (profile != null && !profile.isEmpty()) {
            contentCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getName(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }
        User user = userService.create(userCreateRequest, contentCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.ok(user, "사용자가 생성되었습니다"));
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PATCH,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResult<User>> userUpdate(
            @PathVariable("id") UUID id,
            @RequestPart("user") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile) throws IOException {
        Optional<BinaryContentCreateRequest> contentCreateRequest = Optional.empty();

        if (profile != null && !profile.isEmpty()) {
            contentCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getName(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }

        User updatedUser = userService.update(id, userUpdateRequest, contentCreateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(updatedUser, "수정 완료되었습니다"));
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<ApiResult<String>> userDelete(@PathVariable("id") UUID id){
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok("사용자 "+id+" 가 삭제되었습니다"));
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<ApiResult<List<UserDto>>> findAllUser(){
        List<UserDto> users = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(users));
    }
}
