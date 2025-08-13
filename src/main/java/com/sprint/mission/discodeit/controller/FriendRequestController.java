package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.FriendRequestHandleRequest;
import com.sprint.mission.discodeit.dto.request.FriendRequestSendRequest;
import com.sprint.mission.discodeit.dto.response.FriendRequestResponse;
import com.sprint.mission.discodeit.service.FriendRequestService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend-requests")
public class FriendRequestController {

    private final UserService userService;
    private final FriendRequestService friendRequestService;

    @GetMapping
    public ResponseEntity<List<FriendRequestResponse>> findAll() {
        return ResponseEntity.ok(friendRequestService.findAll());
    }

    @GetMapping(path = "/sent")
    public ResponseEntity<List<FriendRequestResponse>> findAllBySenderId(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(friendRequestService.findAllBySenderId(userId));
    }

    @GetMapping(path = "/received")
    public ResponseEntity<List<FriendRequestResponse>> findAllByReceiverId(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(friendRequestService.findAllByReceiverId(userId));
    }

    @PostMapping
    public ResponseEntity<FriendRequestResponse> sendFriendRequest(@Valid @RequestBody FriendRequestSendRequest body) {
        FriendRequestResponse saved = friendRequestService.send(body);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @DeleteMapping(path = "/by-user/{userId}")
    public ResponseEntity<Void> clearFriendRequests(@PathVariable("userId") UUID userId) {
        friendRequestService.clearFriendRequests(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{id}/accept")
    public ResponseEntity<Void> accept(@PathVariable("id") UUID requestId,
                                       @Valid @RequestBody FriendRequestHandleRequest body) {
        friendRequestService.accept(requestId, body.userId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable("id") UUID requestId,
                                       @Valid @RequestBody FriendRequestHandleRequest body) {
        friendRequestService.reject(requestId, body.userId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable("id") UUID requestId,
                                       @Valid @RequestBody FriendRequestHandleRequest body) {
        friendRequestService.cancel(requestId, body.userId());
        return ResponseEntity.noContent().build();
    }
}
