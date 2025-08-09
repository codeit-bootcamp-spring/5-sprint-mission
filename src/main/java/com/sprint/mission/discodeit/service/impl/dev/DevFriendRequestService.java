package com.sprint.mission.discodeit.service.impl.dev;

import com.sprint.mission.discodeit.domain.deventity.DevFriendRequest;
import com.sprint.mission.discodeit.domain.deventity.DevUser;
import com.sprint.mission.discodeit.repository.devrepository.DevFriendRequestRepository;
import com.sprint.mission.discodeit.repository.devrepository.DevUserRepository;
import com.sprint.mission.discodeit.service.FriendRequestService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Profile("test")
public class DevFriendRequestService implements FriendRequestService {

    private final DevUserRepository userRepository;
    private final DevFriendRequestRepository friendRequestRepository;

    public DevFriendRequestService(DevUserRepository userRepository, DevFriendRequestRepository friendRequestRepository) {
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
    }

    protected void update(UUID id, Consumer<DevFriendRequest> updater) {
        DevFriendRequest entity = friendRequestRepository.getOrThrow(id);
        updater.accept(entity);
        friendRequestRepository.save(entity);
    }

    @Override
    public void send(UUID sender, UUID receiver) {
        DevFriendRequest friendRequest = new DevFriendRequest(sender, receiver);
        if (!friendRequestRepository.existsBySenderIdAndReceiverId(sender, receiver))
            friendRequestRepository.save(friendRequest);
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
    public void decline(UUID requestId) {
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
