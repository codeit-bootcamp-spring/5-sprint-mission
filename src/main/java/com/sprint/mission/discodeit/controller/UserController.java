package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserFindResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
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

@RestController
@RestControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final BasicUserService userService;
    private final BasicUserStatusService userStatusService;

    @RequestMapping(path = "/create",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> create(
            @RequestPart UserCreateRequest userCreateRequest,
            @RequestPart(required = false) MultipartFile profileImage
    ) throws IOException {
        Optional<BinaryContentCreateRequest> binaryRequest = Optional.empty();
        if (!profileImage.isEmpty()) {
            binaryRequest = Optional.of(new BinaryContentCreateRequest(
                    profileImage.getName(),
                    profileImage.getContentType(),
                    profileImage.getBytes()
            ));
        }
        User createdUser = userService.create(userCreateRequest, binaryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @RequestMapping(path = "/update",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> update(
            @RequestPart UserUpdateRequest userUpdateRequest,
            @RequestPart(required = false) MultipartFile profileImage
    ) throws IOException {
        Optional<BinaryContentCreateRequest> binaryRequest = Optional.empty();
        if (!profileImage.isEmpty()) {
            binaryRequest = Optional.of(new BinaryContentCreateRequest(
                    profileImage.getName(),
                    profileImage.getContentType(),
                    profileImage.getBytes()
            ));
        }
        User updatedUser = userService.update(userUpdateRequest, binaryRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @RequestMapping(path = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<User> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(path = {"/list", "/findAll"}, method = RequestMethod.GET)
    public ResponseEntity<List<UserFindResponse>> list() {
        List<UserFindResponse> users = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @RequestMapping(path = "/updateUserStatus", method = RequestMethod.POST)
    public ResponseEntity<UserStatus> updateUserStatus(@RequestPart UserStatusUpdateRequest userStatusUpdateRequest) {
        UserStatus updatedStatus = userStatusService.updateByUserId(userStatusUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updatedStatus);
    }
}
