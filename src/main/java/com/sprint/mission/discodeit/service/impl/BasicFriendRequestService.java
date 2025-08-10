package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.domain.entity.FriendRequest;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.repository.FriendRequestRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.FriendRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Profile({"test", "dev"})
public class BasicFriendRequestService implements FriendRequestService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    protected void update(UUID id, Consumer<FriendRequest> updater) {
        FriendRequest entity = friendRequestRepository.getOrThrow(id);
        updater.accept(entity);
        friendRequestRepository.save(entity);
    }

    public List<FriendRequest> listSent(UUID userId) {
        if (userId == null) return List.of();
        return friendRequestRepository.getSentRequests(userId).stream()
                .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
                .toList();
    }

    public List<FriendRequest> listReceived(UUID userId) {
        if (userId == null) return List.of();
        return friendRequestRepository.getReceivedRequests(userId).stream()
                .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
                .toList();
    }

    public List<FriendRequest> listAllMine(UUID userId) {
        if (userId == null) return List.of();
        var seen = new HashSet<UUID>();
        var sent = friendRequestRepository.getSentRequests(userId);
        var received = friendRequestRepository.getReceivedRequests(userId);
        return Stream.concat(sent.stream(), received.stream())
                .filter(fr -> seen.add(fr.getId()))
                .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public FriendRequest send(UUID sender, UUID receiver) {
        userRepository.getOrThrow(sender);
        userRepository.getOrThrow(receiver);
        FriendRequest friendRequest = new FriendRequest(sender, receiver);
        if (userRepository.getOrThrow(sender).isFriend(receiver))
            throw new IllegalArgumentException("이미 친구입니다.");
        if (friendRequestRepository.existsBySenderIdAndReceiverId(sender, receiver)
                || friendRequestRepository.existsBySenderIdAndReceiverId(receiver, sender))
            throw new IllegalArgumentException("친구 요청이 이미 존재합니다.");
        return friendRequestRepository.save(friendRequest);
    }

    @Override
    public void accept(UUID requestId) {
        FriendRequest fr = friendRequestRepository.getOrThrow(requestId);
        UUID senderId = fr.getSenderId();
        UUID receiverId = fr.getReceiverId();
        User sender = userRepository.getOrThrow(senderId);
        User receiver = userRepository.getOrThrow(receiverId);
        sender.addFriend(receiverId);
        receiver.addFriend(senderId);
        userRepository.save(sender);
        userRepository.save(receiver);
        friendRequestRepository.hardDeleteById(requestId);
        friendRequestRepository.getSentRequests(receiverId).stream()
                .filter(x -> x.getReceiverId().equals(senderId))
                .map(FriendRequest::getId)
                .forEach(friendRequestRepository::hardDeleteById);
    }

    @Override
    public void reject(UUID requestId) {
        if (friendRequestRepository.findById(requestId).isEmpty()) throw new NoSuchElementException("이미 처리된 요청입니다.");
        friendRequestRepository.hardDeleteById(requestId);
    }

    @Override
    public void clear(UUID userId) {
        if (userId == null) return;
        friendRequestRepository.clear(userId);
    }
}
