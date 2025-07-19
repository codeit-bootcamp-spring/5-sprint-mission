package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.FriendRequest;
import com.sprint.mission.discodeit.enums.user.FriendRequestStatus;
import com.sprint.mission.discodeit.service.FriendRequestService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class JcfFriendRequestService extends JcfService<FriendRequest>
    implements FriendRequestService {
  private static final JcfFriendRequestService instance = new JcfFriendRequestService();

  private JcfFriendRequestService() {}

  public static JcfFriendRequestService getInstance() {
    return instance;
  }

  private final Map<UUID, Set<UUID>> sentIndex = new HashMap<>();
  private final Map<UUID, Set<UUID>> receivedIndex = new HashMap<>();

  private final UserService userService = JcfUserService.getInstance();

  private boolean hasPendingRequest(UUID senderId, UUID receiverId) {
    return data.stream()
        .anyMatch(
            fr ->
                fr.getStatus() == FriendRequestStatus.PENDING
                    && ((fr.getSenderId().equals(senderId) && fr.getReceiverId().equals(receiverId))
                        || (fr.getSenderId().equals(receiverId)
                            && fr.getReceiverId().equals(senderId))));
  }

  @Override
  public FriendRequest sendFriendRequest(UUID senderId, UUID receiverId) {
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

    return request;
  }

  @Override
  public void acceptFriendRequest(UUID requestId) {
    FriendRequest fr = requireEntity(requestId);
    if (fr.getStatus() != FriendRequestStatus.PENDING) {
      throw new IllegalStateException("이미 처리된 친구 요청입니다.");
    }

    fr.setStatus(FriendRequestStatus.ACCEPTED);
    fr.setUpdatedAt(System.currentTimeMillis());

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

    data.remove(fr);
    sentIndex.getOrDefault(fr.getSenderId(), Set.of()).remove(fr.getReceiverId());
    receivedIndex.getOrDefault(fr.getReceiverId(), Set.of()).remove(fr.getSenderId());
  }

  @Override
  public List<FriendRequest> getPendingRequestsForUser(UUID userId) {
    return data.stream()
        .filter(
            fr ->
                fr.getReceiverId().equals(userId) && fr.getStatus() == FriendRequestStatus.PENDING)
        .collect(Collectors.toList());
  }

  public List<FriendRequest> getSentRequests(UUID senderId) {
    return data.stream()
        .filter(fr -> fr.getSenderId().equals(senderId))
        .collect(Collectors.toList());
  }

  public List<FriendRequest> getReceivedRequests(UUID receiverId) {
    return data.stream()
        .filter(fr -> fr.getReceiverId().equals(receiverId))
        .collect(Collectors.toList());
  }
}
