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

  private final UserService userService;

  private final Map<UUID, Set<UUID>> sentIndex = new HashMap<>();
  private final Map<UUID, Set<UUID>> receivedIndex = new HashMap<>();

  private JcfFriendRequestService() {
    this.userService = JcfUserService.getInstance();
  }

  public static JcfFriendRequestService getInstance() {
    return instance;
  }

  @Override
  public void deleteById(UUID id) {
    FriendRequest fr = findById(id);

    super.deleteById(id);

    if (fr != null) {
      Optional.ofNullable(sentIndex.get(fr.getSenderId()))
          .ifPresent(
              set -> {
                set.remove(fr.getReceiverId());
                if (set.isEmpty()) {
                  sentIndex.remove(fr.getSenderId());
                }
              });
      Optional.ofNullable(receivedIndex.get(fr.getReceiverId()))
          .ifPresent(
              set -> {
                set.remove(fr.getSenderId());
                if (set.isEmpty()) {
                  receivedIndex.remove(fr.getReceiverId());
                }
              });
    }
  }

  @Override
  public FriendRequest save(FriendRequest friendRequest) {
    UUID senderId = friendRequest.getSenderId();
    UUID receiverId = friendRequest.getReceiverId();

    userService.getOrThrow(senderId);
    User receiver = userService.getOrThrow(receiverId);

    if (findById(friendRequest.getId()) != null) {
      throw new IllegalArgumentException("중복된 id가 존재합니다.");
    }

    if (senderId.equals(receiverId)) {
      throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
    }

    boolean alreadySent =
        sentIndex.getOrDefault(senderId, Collections.emptySet()).contains(receiverId);
    boolean alreadyReceived =
        receivedIndex.getOrDefault(senderId, Collections.emptySet()).contains(receiverId);

    if (userService.getFriends(senderId).contains(receiver)) {
      throw new IllegalArgumentException("이미 친구입니다.");
    }

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
    FriendRequest fr = findById(requestId);
    if (fr == null) {
      throw new NoSuchElementException("이미 처리된 요청입니다.");
    }

    userService.addFriend(fr.getSenderId(), fr.getReceiverId());
    userService.addFriend(fr.getReceiverId(), fr.getSenderId());

    deleteById(requestId);
  }

  @Override
  public void declineFriendRequest(UUID requestId) {
    if (findById(requestId) == null) {
      throw new NoSuchElementException("이미 처리된 요청입니다.");
    }

    deleteById(requestId);
  }

  @Override
  public void clearFriendRequests(UUID userId) {
    Set<UUID> alreadyDeleted = new HashSet<>();

    List<FriendRequest> requestsToDelete = new ArrayList<>();
    requestsToDelete.addAll(getReceivedRequests(userId));
    requestsToDelete.addAll(getSentRequests(userId));

    requestsToDelete.stream()
        .filter(fr -> alreadyDeleted.add(fr.getId()))
        .forEach(fr -> deleteById(fr.getId()));
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
