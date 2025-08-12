package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.FriendRequestDeleteRequest;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend-requests")
public class FriendRequestController {

    private final UserService userService;
    private final FriendRequestService friendRequestService;

    @GetMapping
    public ResponseEntity<List<FriendRequestResponse>> findAllByUserId(@RequestParam UUID id,
                                                                       @RequestParam String direction) {
        return ResponseEntity.ok(friendRequestService.findAllByUserId(id, direction));
    }

    @PostMapping
    public ResponseEntity<FriendRequestResponse> sendFriendRequest(@Valid @RequestBody FriendRequestSendRequest body) {
        FriendRequestResponse saved = friendRequestService.send(body);
        return ResponseEntity
                .created(URI.create("/api/friend-requests/"))
                .body(saved);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllFriendRequests(FriendRequestDeleteRequest body) {
        friendRequestService.deleteAllByUserId(body.userId());
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.POST)
    public ResponseEntity<Void> handleFriendRequest(@PathVariable("id") UUID requestId,
                                                    @RequestBody FriendRequestHandleRequest body) {
        String s = Objects.requireNonNull(body.status()).strip().toUpperCase();
        if (!s.equals("ACCEPTED") && !s.equals("REJECTED")) return ResponseEntity.badRequest().build();
        if (s.equals("ACCEPTED")) friendRequestService.accept(requestId);
        else friendRequestService.reject(requestId);
        return ResponseEntity.noContent().build();
    }
}
