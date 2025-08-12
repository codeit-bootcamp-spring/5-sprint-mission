package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(value = "/user", method = RequestMethod.POST,
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto.DetailResponse> createUser(@ModelAttribute UserDto.CreateRequest request) {
        return ResponseEntity.ok(userService.create(request));
    }

    @RequestMapping(value = "/user", method = RequestMethod.PUT,
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto.DetailResponse> updateUser(@ModelAttribute UserDto.UpdateRequest request) {
        return ResponseEntity.ok(userService.update(request));
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/user/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto.DetailResponse>> findAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @RequestMapping(value = "/user/{id}/status", method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateUserStatus(@PathVariable UUID id) {
        userStatusService.updateByUserId(id);
        return ResponseEntity.ok().build();
    }
}
