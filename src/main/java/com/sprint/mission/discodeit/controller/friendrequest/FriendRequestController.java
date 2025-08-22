package com.sprint.mission.discodeit.controller.friendrequest;

import com.sprint.mission.discodeit.domain.enums.FriendRequestAction;
import com.sprint.mission.discodeit.dto.request.friendrequest.FriendRequestHandleRequest;
import com.sprint.mission.discodeit.dto.request.friendrequest.FriendRequestSendRequest;
import com.sprint.mission.discodeit.dto.response.friendrequest.FriendRequestResponse;
import com.sprint.mission.discodeit.service.friendrequest.FriendRequestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend-requests")
public class FriendRequestController {

  private final FriendRequestService friendRequestService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<FriendRequestResponse> findAll() {
    return friendRequestService.findAll();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public FriendRequestResponse find(
      @PathVariable("id") @NotNull UUID id) {
    return friendRequestService.findById(id);
  }

  @GetMapping(path = "/sent")
  @ResponseStatus(HttpStatus.OK)
  public List<FriendRequestResponse> findAllBySenderId(@RequestParam("userId") UUID userId) {
    return friendRequestService.findAllBySenderId(userId);
  }

  @GetMapping(path = "/received")
  @ResponseStatus(HttpStatus.OK)
  public List<FriendRequestResponse> findAllByReceiverId(@RequestParam("userId") UUID userId) {
    return friendRequestService.findAllByReceiverId(userId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public FriendRequestResponse send(@Valid @RequestBody FriendRequestSendRequest body) {
    return friendRequestService.send(body);
  }

  @DeleteMapping(path = "/by-user/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void clearFriendRequests(@PathVariable("userId") UUID userId) {
    friendRequestService.clearFriendRequests(userId);
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void accept(@PathVariable("id") UUID requestId,
      @RequestParam(name = "action") FriendRequestAction action,
      @Valid @RequestBody FriendRequestHandleRequest body) {
    switch (action) {
      case ACCEPT -> friendRequestService.accept(requestId, body.userId());
      case REJECT -> friendRequestService.reject(requestId, body.userId());
      case CANCEL -> friendRequestService.cancel(requestId, body.userId());
      default -> throw new IllegalArgumentException("지원하지 않는 action입니다: " + action);
    }
  }
}
