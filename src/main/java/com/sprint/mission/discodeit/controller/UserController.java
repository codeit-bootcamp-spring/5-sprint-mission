package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.AddUserRequest;
import com.sprint.mission.discodeit.dto.response.GetUserResponse;
import com.sprint.mission.discodeit.dto.response.UpdateStatusUserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<User> registerUser(@RequestBody AddUserRequest addUserRequest) {
        User user = userService.addUser(addUserRequest);
        return ResponseEntity.ok(user);
    }

    @RequestMapping(path="/findAll",method = RequestMethod.GET)
    public ResponseEntity<List<GetUserResponse>> getAllUsers() {
        List<GetUserResponse> allUser = userService.getAllUser();
        return ResponseEntity.ok(allUser);
    }

    @RequestMapping(path = "/{userId}",method = RequestMethod.POST)
    public ResponseEntity<User> updateUser(
            @PathVariable UUID userId,
            @RequestBody AddUserRequest addUserRequest) {
        User user = userService.updateUser(userId, addUserRequest);
        return ResponseEntity.ok(user);
    }

    @RequestMapping(path="/{userId}", method=RequestMethod.DELETE)
    public void deleteUser(@RequestBody UUID userId) {
        userService.deleteUser(userId);
    }

    @RequestMapping(path= "/{userId}/update", method=RequestMethod.POST)
    public UpdateStatusUserResponse updateStatusUser(@PathVariable UUID userId) {
        UserStatus userStatus = userStatusService.updateUserStatusByUserId(userId);
        return new UpdateStatusUserResponse(userId, userStatus.isOnline());
    }

}
