package com.sprint.mission.discodeit.service.impl.dev;

import com.sprint.mission.discodeit.domain.deventity.DevFriendRequest;
import com.sprint.mission.discodeit.domain.deventity.DevUser;
import com.sprint.mission.discodeit.repository.devrepository.DevFriendRequestRepository;
import com.sprint.mission.discodeit.repository.devrepository.DevUserRepository;
import com.sprint.mission.discodeit.service.dev.DevFriendRequestService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
@Profile({"test", "dev"})
public class BasicFriendRequestService implements DevFriendRequestService {

    private final DevUserRepository userRepository;
    private final DevFriendRequestRepository friendRequestRepository;

    public BasicFriendRequestService(DevUserRepository userRepository, DevFriendRequestRepository friendRequestRepository) {
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
    }

    protected void update(UUID id, Consumer<DevFriendRequest> updater) {
        DevFriendRequest entity = friendRequestRepository.getOrThrow(id);
        updater.accept(entity);
        friendRequestRepository.save(entity);
    }

    public List<DevFriendRequest> listSent(UUID userId) {
        if (userId == null) return List.of();
        return friendRequestRepository.getSentRequests(userId).stream()
                .sorted(Comparator.comparing(DevFriendRequest::getCreatedAt).reversed())
                .toList();
    }

    public List<DevFriendRequest> listReceived(UUID userId) {
        if (userId == null) return List.of();
        return friendRequestRepository.getReceivedRequests(userId).stream()
                .sorted(Comparator.comparing(DevFriendRequest::getCreatedAt).reversed())
                .toList();
    }

    public List<DevFriendRequest> listAllMine(UUID userId) {
        if (userId == null) return List.of();
        var seen = new HashSet<UUID>();
        var sent = friendRequestRepository.getSentRequests(userId);
        var received = friendRequestRepository.getReceivedRequests(userId);
        return Stream.concat(sent.stream(), received.stream())
                .filter(fr -> seen.add(fr.getId()))
                .sorted(Comparator.comparing(DevFriendRequest::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public DevFriendRequest send(UUID sender, UUID receiver) {
        userRepository.getOrThrow(sender);
        userRepository.getOrThrow(receiver);
        DevFriendRequest friendRequest = new DevFriendRequest(sender, receiver);
        if (friendRequestRepository.existsBySenderIdAndReceiverId(sender, receiver))
            throw new IllegalArgumentException("친구 요청이 이미 존재합니다.");
        return friendRequestRepository.save(friendRequest);
    }

    @Override
    public void accept(UUID requestId) {
        DevFriendRequest fr = friendRequestRepository.getOrThrow(requestId);
        UUID senderId = fr.getSender();
        UUID receiverId = fr.getReceiver();
        DevUser sender = userRepository.getOrThrow(senderId);
        DevUser receiver = userRepository.getOrThrow(receiverId);
        sender.addFriend(receiverId);
        receiver.addFriend(senderId);
        userRepository.save(sender);
        userRepository.save(receiver);
        friendRequestRepository.hardDeleteById(requestId);
    }

    @Override
    public void reject(UUID requestId) {
        if (friendRequestRepository.findById(requestId).isEmpty()) throw new NoSuchElementException("이미 처리된 요청입니다.");
        friendRequestRepository.hardDeleteById(requestId);
    }

    @Override
    public void clear(UUID userId) {
        Set<UUID> frIds = new HashSet<>();
        friendRequestRepository.getReceivedRequests(userId).forEach(fr -> frIds.add(fr.getId()));
        friendRequestRepository.getSentRequests(userId).forEach(fr -> frIds.add(fr.getId()));
        friendRequestRepository.deleteAllByIds(frIds);
    }
}
