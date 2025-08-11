package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.service.BasicFriendRequestService;
import com.sprint.mission.discodeit.service.BasicUserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/friends")
@Profile({"test", "dev"})
public class FriendController {

    private final BasicUserService userService;
    private final BasicFriendRequestService friendRequestService;
    private final UserStatusService userStatusService;

    // @RequestMapping(path = "/{id}/friends", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    // public ResponseEntity<List<UserResponse>> getFriends(@PathVariable("id") UUID userId) {
    //     List<UserResponse> friends = userService.getFriends(userId);
    //     return ResponseEntity.ok(body);
    // }
}
