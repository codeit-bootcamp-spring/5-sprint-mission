package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.FriendRequest;
import com.sprint.mission.discodeit.enums.user.FriendRequestStatus;
import com.sprint.mission.discodeit.service.FriendRequestService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class JcfFriendRequestService extends JcfService<FriendRequest>
    implements FriendRequestService {
  private static final JcfFriendRequestService instance = new JcfFriendRequestService();

  private JcfFriendRequestService() {}

  public static JcfFriendRequestService getInstance() {
    return instance;
  }

  private final Map<UUID, Set<UUID>> sentIndex = new HashMap<>();
  private final Map<UUID, Set<UUID>> receivedIndex = new HashMap<>();

  @Override
  public void update(UUID id, Consumer<FriendRequest> updater) {
    FriendRequest fr = requireEntity(id);
    updater.accept(fr);
    fr.setUpdatedAt(System.currentTimeMillis());
  }

  @Override
  public FriendRequest sendFriendRequest(UUID senderId, UUID receiverId) {
    if (senderId.equals(receiverId)) {
      throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
    }

    boolean alreadyExists =
        data.stream()
            .anyMatch(
                fr ->
                    (fr.getSenderId().equals(senderId) && fr.getReceiverId().equals(receiverId)
                            || fr.getSenderId().equals(receiverId)
                                && fr.getReceiverId().equals(senderId))
                        && fr.getStatus() == FriendRequestStatus.PENDING);

    if (alreadyExists) {
      throw new IllegalStateException("이미 친구 요청이 존재합니다.");
    }

    FriendRequest request = new FriendRequest(senderId, receiverId);
    data.add(request);
    return request;
  }

  @Override
  public void acceptFriendRequest(UUID requestId) {
    FriendRequest request = requireEntity(requestId);
    if (request.getStatus() != FriendRequestStatus.PENDING) {
      throw new IllegalStateException("이미 처리된 요청입니다.");
    }

    request.setStatus(FriendRequestStatus.ACCEPTED);
    request.setUpdatedAt(System.currentTimeMillis());
  }

  @Override
  public void declineFriendRequest(UUID requestId) {
    FriendRequest request = requireEntity(requestId);
    if (request.getStatus() != FriendRequestStatus.PENDING) {
      throw new IllegalStateException("이미 처리된 요청입니다.");
    }

    request.setStatus(FriendRequestStatus.DECLINED);
    request.setUpdatedAt(System.currentTimeMillis());
  }

  @Override
  public void cancelFriendRequest(UUID requestId) {
    FriendRequest request = requireEntity(requestId);
    if (request.getStatus() != FriendRequestStatus.PENDING) {
      throw new IllegalStateException("이미 처리된 요청입니다.");
    }

    data.remove(request);
  }

  @Override
  public List<FriendRequest> getPendingRequestsForUser(UUID userId) {
    return data.stream()
        .filter(
            fr ->
                fr.getReceiverId().equals(userId) && fr.getStatus() == FriendRequestStatus.PENDING)
        .toList();
  }
}
