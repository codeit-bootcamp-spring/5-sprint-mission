package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.FriendRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.FriendRequestService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class FileFriendRequestService extends BaseFileService<FriendRequest> implements FriendRequestService {
    private final UserService userService;

    public FileFriendRequestService(UserService userService) {
        super(FriendRequest.class);
        this.userService = userService;
    }

    @Override
    public FriendRequest save(FriendRequest friendRequest) {
        if (existsById(friendRequest.getId())) throw new IllegalArgumentException("중복된 id가 존재합니다.");
        UUID senderId = friendRequest.getSenderId();
        UUID receiverId = friendRequest.getReceiverId();
        if (senderId.equals(receiverId)) throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        userService.getOrThrow(senderId);
        User receiver = userService.getOrThrow(receiverId);
        if (userService.getFriends(senderId).contains(receiver)) throw new IllegalArgumentException("이미 친구입니다.");
        boolean alreadySent = findAll().stream().anyMatch(fr -> (fr.getSenderId().equals(senderId) && fr.getReceiverId().equals(receiverId)) || (fr.getSenderId().equals(receiverId) && fr.getReceiverId().equals(senderId)));
        if (alreadySent) throw new IllegalStateException("이미 친구 요청이 존재합니다.");
        return super.save(friendRequest);
    }

    @Override
    public boolean hardDeleteById(UUID id) {
        Optional<FriendRequest> opt = findById(id);
        if (opt.isEmpty()) {
            throw new NoSuchElementException("해당 ID의 친구 요청을 찾을 수 없습니다.");
        }
        return super.hardDeleteById(id);
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
        if (findById(requestId).isEmpty()) throw new NoSuchElementException("이미 처리된 요청입니다.");
        hardDeleteById(requestId);
    }

    @Override
    public void clearFriendRequests(UUID userId) {
        Set<UUID> deletedIds = new HashSet<>();
        findAll().stream().filter(fr -> fr.getSenderId().equals(userId) || fr.getReceiverId().equals(userId)).map(FriendRequest::getId).filter(deletedIds::add).forEach(this::hardDeleteById);
    }


    @Override
    public List<FriendRequest> getSentRequests(UUID senderId) {
        return findAll().stream()
                .filter(fr -> fr.getSenderId().equals(senderId))
                .toList();
    }

    @Override
    public List<FriendRequest> getReceivedRequests(UUID receiverId) {
        return findAll().stream()
                .filter(fr -> fr.getReceiverId().equals(receiverId))
                .toList();
    }
}
