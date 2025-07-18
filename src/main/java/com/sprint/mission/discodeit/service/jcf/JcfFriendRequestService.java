package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.FriendRequest;
import com.sprint.mission.discodeit.enums.user.FriendRequestStatus;
import com.sprint.mission.discodeit.service.FriendRequestService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Consumer;

public class JcfFriendRequestService extends JcfService<FriendRequest>
    implements FriendRequestService {
  private static final JcfFriendRequestService instance = new JcfFriendRequestService();

  private JcfFriendRequestService() {}

  public static JcfFriendRequestService getInstance() {
    return instance;
  }

  private FriendRequest requireFriendRequest(UUID id) {
    FriendRequest friendRequest = findById(id);
    if (friendRequest == null) {
      throw new NoSuchElementException("친구 요청을 찾을 수 없습니다 : " + id);
    }
    return friendRequest;
  }

  @Override
  protected boolean idEquals(FriendRequest friendRequest, UUID id) {
    return friendRequest.getId().equals(id);
  }

  @Override
  public void update(UUID id, Consumer<FriendRequest> updater) {
    FriendRequest fr = requireFriendRequest(id);
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
    FriendRequest request = requireFriendRequest(requestId);
    if (request.getStatus() != FriendRequestStatus.PENDING) {
      throw new IllegalStateException("이미 처리된 요청입니다.");
    }

    request.setStatus(FriendRequestStatus.ACCEPTED);
    request.setUpdatedAt(System.currentTimeMillis());
  }

  @Override
  public void declineFriendRequest(UUID requestId) {
    FriendRequest request = requireFriendRequest(requestId);
    if (request.getStatus() != FriendRequestStatus.PENDING) {
      throw new IllegalStateException("이미 처리된 요청입니다.");
    }

    request.setStatus(FriendRequestStatus.DECLINED);
    request.setUpdatedAt(System.currentTimeMillis());
  }

  @Override
  public void cancelFriendRequest(UUID requestId) {
    FriendRequest request = requireFriendRequest(requestId);
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
