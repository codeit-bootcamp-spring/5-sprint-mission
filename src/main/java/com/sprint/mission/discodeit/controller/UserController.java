package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserFindResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@ControllerAdvice
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@RequestBody UserCreateRequest request) {
        User createdUser = userService.create(request);
        return "등록 성공\n" + createdUser.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(@RequestBody UserUpdateRequest request) {
        User updatedUser = userService.update(request);
        return "업데이트 성공\n" + updatedUser.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable UUID id) {
        UserFindResponse userFindResponse = userService.findById(id);
        userService.delete(id);
        return "삭제 성공\n" + userFindResponse.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list() {
        List<UserFindResponse> users = userService.findAll();
        return users.toString().replace("], ", "]\n");
    }

    @ResponseBody
    @RequestMapping(value = "/updateUserStatus", method = RequestMethod.POST)
    public String updateUserStatus(@RequestBody UserStatusUpdateRequest request) {
        UserStatus updatedStatus = userStatusService.updateByUserId(request);
        return userService.findById(updatedStatus.getUserId()).toString();
    }
}
