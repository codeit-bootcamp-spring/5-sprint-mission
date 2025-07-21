package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.FriendRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.FriendRequestService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class JcfFriendRequestService extends BaseJcfService<FriendRequest>
    implements FriendRequestService {
  private static final JcfFriendRequestService instance = new JcfFriendRequestService();

  private UserService userService;

  private final Map<UUID, Set<UUID>> sentIndex = new HashMap<>();
  private final Map<UUID, Set<UUID>> receivedIndex = new HashMap<>();

  private JcfFriendRequestService() {}

  public static JcfFriendRequestService getInstance() {
    return instance;
  }

  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  @Override
  public boolean hardDeleteById(UUID id) {
    Optional<FriendRequest> frOpt = findById(id);

    if (frOpt.isEmpty()) {
      throw new NoSuchElementException("해당 id의 친구 요청을 찾을 수 없습니다.");
    }

    boolean deleted = super.hardDeleteById(id);

    if (deleted) {
      FriendRequest fr = frOpt.get();

      Set<UUID> sentSet = sentIndex.get(fr.getSenderId());
      if (sentSet != null) {
        sentSet.remove(fr.getReceiverId());
        if (sentSet.isEmpty()) {
          sentIndex.remove(fr.getSenderId());
        }
      }

      Set<UUID> receivedSet = receivedIndex.get(fr.getReceiverId());
      if (receivedSet != null) {
        receivedSet.remove(fr.getSenderId());
        if (receivedSet.isEmpty()) {
          receivedIndex.remove(fr.getReceiverId());
        }
      }
    }

    return deleted;
  }

  @Override
  public FriendRequest save(FriendRequest friendRequest) {
    getOrThrow(friendRequest.getId());

    UUID senderId = friendRequest.getSenderId();
    UUID receiverId = friendRequest.getReceiverId();

    if (senderId.equals(receiverId)) {
      throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
    }

    userService.getOrThrow(senderId);
    User receiver = userService.getOrThrow(receiverId);


    if (userService.getFriends(senderId).contains(receiver)) {
      throw new IllegalArgumentException("이미 친구입니다.");
    }

    boolean alreadySent =
        sentIndex.getOrDefault(senderId, Collections.emptySet()).contains(receiverId);
    boolean alreadyReceived =
        sentIndex.getOrDefault(receiverId, Collections.emptySet()).contains(senderId);


    if (alreadySent || alreadyReceived) {
      throw new IllegalStateException("이미 친구 요청이 존재합니다.");
    }

    data.add(friendRequest);
    sentIndex.computeIfAbsent(senderId, k -> new HashSet<>()).add(receiverId);
    receivedIndex.computeIfAbsent(receiverId, k -> new HashSet<>()).add(senderId);
    return friendRequest;
  }

  @Override
  public void acceptFriendRequest(UUID requestId) {
    FriendRequest fr = getOrThrow(requestId);

    userService.addFriend(fr.getSenderId(), fr.getReceiverId());
    userService.addFriend(fr.getReceiverId(), fr.getSenderId());

    hardDeleteById(requestId);
  }

  @Override
  public void declineFriendRequest(UUID requestId) {
    if (findById(requestId).isEmpty()) {
      throw new NoSuchElementException("이미 처리된 요청입니다.");
    }

    hardDeleteById(requestId);
  }

  @Override
  public void clearFriendRequests(UUID userId) {
    Set<UUID> alreadyDeleted = new HashSet<>();

    List<FriendRequest> requestsToDelete = new ArrayList<>();
    requestsToDelete.addAll(getReceivedRequests(userId));
    requestsToDelete.addAll(getSentRequests(userId));

    requestsToDelete.stream()
        .filter(fr -> alreadyDeleted.add(fr.getId()))
        .forEach(fr -> hardDeleteById(fr.getId()));
  }

  @Override
  public List<FriendRequest> getSentRequests(UUID senderId) {
    return data.stream()
        .filter(fr -> fr.getSenderId().equals(senderId))
        .collect(Collectors.toList());
  }

  @Override
  public List<FriendRequest> getReceivedRequests(UUID receiverId) {
    return data.stream()
        .filter(fr -> fr.getReceiverId().equals(receiverId))
        .collect(Collectors.toList());
  }
}
