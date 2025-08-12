// package com.sprint.mission.discodeit.controller;
//
// import com.sprint.mission.discodeit.dto.request.FriendRequestHandleRequest;
// import com.sprint.mission.discodeit.dto.request.FriendRequestSendRequest;
// import com.sprint.mission.discodeit.dto.response.FriendRequestResponse;
// import com.sprint.mission.discodeit.dto.response.UserResponse;
// import com.sprint.mission.discodeit.service.FriendRequestService;
// import com.sprint.mission.discodeit.service.UserService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestMethod;
// import org.springframework.web.bind.annotation.RestController;
//
// import java.net.URI;
// import java.util.List;
// import java.util.Objects;
// import java.util.UUID;
//
// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/api/friend-requests")
// public class FriendRequestController {
//
//     private final UserService userService;
//     private final FriendRequestService friendRequestService;
//
//     // 쿼리스트링으로 ?userId={id}&direction=sent|received
//     @RequestMapping(path = "/{userId}/friends", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//     public ResponseEntity<List<UserResponse>> getFriends(@PathVariable("userId") UUID userId) {
//         return ResponseEntity.ok(userService.getFriends(userId));
//     }
//
//     // body로 senderId
//     @RequestMapping(path = "/{userId}/friend-requests", method = RequestMethod.POST,
//             consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//     public ResponseEntity<FriendRequestResponse> sendFriendRequest(@PathVariable("userId") UUID senderId,
//                                                                    @RequestBody FriendRequestSendRequest body) {
//         FriendRequestResponse saved = friendRequestService.send(senderId, body.receiverUsername());
//         return ResponseEntity
//                 .created(URI.create("/api/friends/" + senderId + "/friend-requests/" + body.receiverUsername()))
//                 .body(saved);
//     }
//
//
//     @RequestMapping(path = "/{id}/friend-requests", method = RequestMethod.DELETE)
//     public ResponseEntity<Void> clearFriendRequests(@PathVariable("id") UUID userId) {
//         friendRequestService.clear(userId);
//         return ResponseEntity.noContent().build();
//     }
//
//     @RequestMapping(path = "/{id}/friend-requests/{requestId}", method = RequestMethod.POST)
//     public ResponseEntity<Void> handleFriendRequest(@PathVariable("id") UUID userId,
//                                                     @PathVariable("requestId") UUID requestId,
//                                                     @RequestBody FriendRequestHandleRequest body) {
//         String status = Objects.requireNonNull(body.status(), "status must not be null");
//         if (!status.equals("ACCEPTED") && !status.equals("REJECTED")) return ResponseEntity.badRequest().build();
//         if (body.status().equals("ACCEPTED")) friendRequestService.accept(requestId);
//         else friendRequestService.reject(requestId);
//         return ResponseEntity.noContent().build();
//     }
// }
