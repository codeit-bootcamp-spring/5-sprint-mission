package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.FriendRequest;
import com.sprint.mission.discodeit.enums.friend.FriendRequestStatus;
import com.sprint.mission.discodeit.service.FriendRequestService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class JcfFriendRequestService extends JcfService<FriendRequest>
    implements FriendRequestService {
  private static final JcfFriendRequestService instance = new JcfFriendRequestService();

  public static JcfFriendRequestService getInstance() {
    return instance;
  }

  private final Map<UUID, Set<UUID>> sentIndex = new HashMap<>();
  private final Map<UUID, Set<UUID>> receivedIndex = new HashMap<>();

  private JcfFriendRequestService() {}

  private boolean hasPendingRequest(UUID userA, UUID userB) {
    return data.stream()
        .anyMatch(
            fr ->
                fr.getStatus() == FriendRequestStatus.PENDING
                    && ((fr.getSenderId().equals(userA) && fr.getReceiverId().equals(userB))
                        || (fr.getSenderId().equals(userB) && fr.getReceiverId().equals(userA))));
  }

  private void removeFromIndex(FriendRequest fr) {
    Optional.ofNullable(sentIndex.get(fr.getSenderId()))
        .ifPresent(set -> set.remove(fr.getReceiverId()));
    Optional.ofNullable(receivedIndex.get(fr.getReceiverId()))
        .ifPresent(set -> set.remove(fr.getSenderId()));
  }

  @Override
  public void deleteById(UUID id) {
    FriendRequest request = findById(id);
    if (request != null) {
      removeFromIndex(request);
    }
    super.deleteById(id);
  }

  @Override
  public void sendFriendRequest(UUID senderId, UUID receiverId) {
    if (senderId.equals(receiverId)) {
      throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
    }
    if (hasPendingRequest(senderId, receiverId)) {
      throw new IllegalStateException("이미 친구 요청이 존재합니다.");
    }

    FriendRequest request = new FriendRequest(senderId, receiverId);
    data.add(request);

    sentIndex.computeIfAbsent(senderId, k -> new HashSet<>()).add(receiverId);
    receivedIndex.computeIfAbsent(receiverId, k -> new HashSet<>()).add(senderId);
  }

  @Override
  public void acceptFriendRequest(UUID requestId) {
    FriendRequest fr = requireEntity(requestId);
    if (fr.getStatus() != FriendRequestStatus.PENDING) {
      throw new IllegalStateException("이미 처리된 친구 요청입니다.");
    }

    fr.setStatus(FriendRequestStatus.ACCEPTED);
    fr.setUpdatedAt(System.currentTimeMillis());

    UserService userService = JcfUserService.getInstance();

    userService.addFriend(fr.getSenderId(), fr.getReceiverId());
    userService.addFriend(fr.getReceiverId(), fr.getSenderId());
  }

  @Override
  public void declineFriendRequest(UUID requestId) {
    FriendRequest fr = requireEntity(requestId);
    if (fr.getStatus() != FriendRequestStatus.PENDING) {
      throw new IllegalStateException("이미 처리된 친구 요청입니다.");
    }

    fr.setStatus(FriendRequestStatus.DECLINED);
    fr.setUpdatedAt(System.currentTimeMillis());
  }

  @Override
  public void cancelFriendRequest(UUID requestId) {
    FriendRequest fr = requireEntity(requestId);
    if (fr.getStatus() != FriendRequestStatus.PENDING) {
      throw new IllegalStateException("이미 처리된 친구 요청입니다.");
    }

    deleteById(requestId);
  }

  @Override
  public void deleteAllRequestsOfUser(UUID userId) {
    Set<UUID> seen = new HashSet<>();

    List<FriendRequest> all = new ArrayList<>();
    all.addAll(getReceivedRequests(userId));
    all.addAll(getSentRequests(userId));

    for (FriendRequest fr : all) {
      if (seen.add(fr.getId())) {
        try {
          deleteById(fr.getId());
        } catch (Exception e) {
          System.err.println(e.getMessage());
        }
      }
    }
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
