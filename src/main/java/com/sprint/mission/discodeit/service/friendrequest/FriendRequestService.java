package com.sprint.mission.discodeit.service.friendrequest;

import com.sprint.mission.discodeit.domain.entity.FriendRequest;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.dto.request.friendrequest.FriendRequestSendRequest;
import com.sprint.mission.discodeit.dto.response.friendrequest.FriendRequestResponse;
import com.sprint.mission.discodeit.exception.AccessDeniedException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.FriendRequestRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendRequestService {

  private final UserRepository userRepository;
  private final FriendRequestRepository friendRequestRepository;

  public List<FriendRequestResponse> findAll() {
    return friendRequestRepository.findAll().stream()
        .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
        .map(this::toResponseWithUsers)
        .toList();
  }

  public FriendRequestResponse findById(UUID id) {
    FriendRequest fr = friendRequestRepository.getOrThrow(id);
    return toResponseWithUsers(fr);
  }

  public List<FriendRequestResponse> findAllBySenderId(UUID senderId) {
    return friendRequestRepository.findAllBySenderId(senderId).stream()
        .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
        .map(this::toResponseWithUsers)
        .toList();
  }

  public List<FriendRequestResponse> findAllByReceiverId(UUID receiverId) {
    return friendRequestRepository.findAllByReceiverId(receiverId).stream()
        .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
        .map(this::toResponseWithUsers)
        .toList();
  }

  @Transactional
  public FriendRequestResponse send(FriendRequestSendRequest body) {
    User sender = userRepository.findById(body.senderId())
        .orElseThrow(
            () -> new NotFoundException("User with id %s not found".formatted(body.senderId())));
    User receiver = userRepository.findByUsername(body.receiverUsername())
        .orElseThrow(() -> new NotFoundException(
            "User with username %s not found".formatted(body.receiverUsername())));

    if (sender.isFriend(receiver.getId())) {
      throw new IllegalArgumentException("Already friends");
    }
    if (existsFriendRequestEitherWay(sender.getId(), receiver.getId())) {
      throw new IllegalArgumentException("Friend request already exists");
    }

    FriendRequest fr = friendRequestRepository.save(
        new FriendRequest(sender.getId(), receiver.getId()));
    return FriendRequestResponse.from(fr, sender, receiver);
  }

  @Transactional
  public void accept(UUID requestId, UUID actingUserId) {
    FriendRequest fr = friendRequestRepository.findById(requestId)
        .orElseThrow(() -> new NotFoundException("이미 처리된 요청입니다"));

    UUID senderId = fr.getSenderId();
    UUID receiverId = fr.getReceiverId();

    if (!receiverId.equals(actingUserId)) {
      throw new AccessDeniedException("친구 요청 수락 권한이 없습니다.");
    }

    Map<UUID, User> users = loadUsers(senderId, receiverId);
    User sender = users.get(senderId);
    User receiver = users.get(receiverId);

    if (!sender.isFriend(receiverId)) {
      sender.addFriend(receiverId);
      userRepository.save(sender);
    }
    if (!receiver.isFriend(senderId)) {
      receiver.addFriend(senderId);
      userRepository.save(receiver);
    }

    friendRequestRepository.hardDelete(requestId);
    friendRequestRepository.hardDeleteBySenderAndReceiver(receiverId, senderId);
  }

  @Transactional
  public void reject(UUID requestId, UUID actingUserId) {
    FriendRequest fr = friendRequestRepository.findById(requestId)
        .orElseThrow(() -> new NotFoundException("이미 처리된 요청입니다."));
    if (!fr.getReceiverId().equals(actingUserId)) {
      throw new AccessDeniedException("친구 요청 거절 권한이 없습니다.");
    }
    friendRequestRepository.hardDelete(requestId);
  }

  @Transactional
  public void cancel(UUID requestId, UUID actingUserId) {
    FriendRequest fr = friendRequestRepository.findById(requestId)
        .orElseThrow(() -> new NotFoundException("이미 처리된 요청입니다."));
    if (!fr.getSenderId().equals(actingUserId)) {
      throw new AccessDeniedException("친구 요청 취소 권한이 없습니다.");
    }
    friendRequestRepository.delete(requestId);
  }

  @Transactional
  public void clearFriendRequests(UUID userId) {
    friendRequestRepository.deleteAllByUserId(userId);
  }

  private boolean existsFriendRequestEitherWay(UUID a, UUID b) {
    return friendRequestRepository.existsBySenderIdAndReceiverId(a, b)
        || friendRequestRepository.existsBySenderIdAndReceiverId(b, a);
  }

  private Map<UUID, User> loadUsers(UUID... ids) {
    Set<UUID> set = new HashSet<>(Arrays.asList(ids));
    Map<UUID, User> map = userRepository.findAllByIdIn(set).stream()
        .collect(Collectors.toMap(User::getId, Function.identity()));
    for (UUID id : set) {
      if (!map.containsKey(id)) {
        throw new NotFoundException("User with id %s not found".formatted(id));
      }
    }
    return map;
  }

  private FriendRequestResponse toResponseWithUsers(FriendRequest fr) {
    Map<UUID, User> users = loadUsers(fr.getSenderId(), fr.getReceiverId());
    return FriendRequestResponse.from(
        fr,
        users.get(fr.getSenderId()),
        users.get(fr.getReceiverId())
    );
  }
}
