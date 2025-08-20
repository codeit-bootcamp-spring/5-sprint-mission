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
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value="/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(path = "create",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> createUser(
            @RequestPart(required = false) MultipartFile profile,
            @RequestPart UserCreateRequest userCreateRequest
    ) throws IOException {
        Optional<BinaryContentCreateRequest> profileCreateRequest = Optional.empty();
        if(!profile.isEmpty()) {
            profileCreateRequest = Optional.of(new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            ));
        }
        User user = userService.create(userCreateRequest, profileCreateRequest);
        userStatusService.create(new UserStatusCreateRequest( user.getId(),user.getCreatedAt()));
        return ResponseEntity.status(201).body(user);
    }

    @RequestMapping(path = "update", method = RequestMethod.POST)
    public ResponseEntity<User> updateUser(@RequestParam("userId") UUID userId,
                                              @RequestPart UserUpdateRequest userUpdateRequest,
                                              @RequestPart(required = false) Optional<BinaryContentCreateRequest> profileCreateRequest

    ){
        User user=userService.update(userId,userUpdateRequest,profileCreateRequest);
        return ResponseEntity.status(200).body(user);
    }

    @RequestMapping(path="delete",method =RequestMethod.DELETE)
    public ResponseEntity<User> deleteUser(@RequestParam("userId") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.status(200).body(null);
    }


    @RequestMapping(path = "findAll")
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }

    @RequestMapping(path="online",method =RequestMethod.POST)
    public ResponseEntity<UserStatus> updateOnline(@RequestParam("userId") UUID userId,
                                             @RequestParam("time")Instant time
                                             ){

        UserStatus userStatus=userStatusService.updateByUserId(userId,new UserStatusUpdateRequest(time) );
        userStatus.isOnline();
        return ResponseEntity.status(200).body(userStatus);

    }


}
