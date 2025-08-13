package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.entity.FriendRequest;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.dto.request.FriendRequestSendRequest;
import com.sprint.mission.discodeit.dto.response.FriendRequestResponse;
import com.sprint.mission.discodeit.exception.AccessDeniedException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.FriendRequestRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.sprint.mission.discodeit.mapper.FriendRequestMapper.toFriendRequestResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendRequestService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final UserStatusRepository userStatusRepository;

    protected void update(UUID id, Consumer<FriendRequest> updater) {
        FriendRequest entity = friendRequestRepository.getOrThrow(id);
        updater.accept(entity);
        friendRequestRepository.save(entity);
    }

    public List<FriendRequestResponse> findAll() {
        return friendRequestRepository.findAll().stream()
                .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
                .map(fr -> toFriendRequestResponse(
                        fr,
                        userRepository.getOrThrow(fr.getSenderId()),
                        userRepository.getOrThrow(fr.getReceiverId())
                ))
                .toList();
    }

    public List<FriendRequestResponse> findAllBySenderId(UUID id) {
        return friendRequestRepository
                .findAllBySenderId(id).stream()
                .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
                .map(fr -> toFriendRequestResponse(
                        fr,
                        userRepository.getOrThrow(fr.getSenderId()),
                        userRepository.getOrThrow(fr.getReceiverId())
                ))
                .toList();
    }

    public List<FriendRequestResponse> findAllByReceiverId(UUID id) {
        return friendRequestRepository
                .findAllByReceiverId(id).stream()
                .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
                .map(fr -> toFriendRequestResponse(
                        fr,
                        userRepository.getOrThrow(fr.getSenderId()),
                        userRepository.getOrThrow(fr.getReceiverId())
                ))
                .toList();
    }

    @Transactional
    public FriendRequestResponse send(FriendRequestSendRequest body) {
        final User sender = userRepository.findById(body.senderId()).orElseThrow(
                () -> new NotFoundException("유저가 존재하지 않습니다: " + body.senderId())
        );
        final User receiver = userRepository.findByUsername(body.receiverUsername()).orElseThrow(
                () -> new NotFoundException("유저가 존재하지 않습니다."));
        if (sender.isFriend(receiver.getId())) {
            throw new IllegalArgumentException("이미 친구입니다.");
        }
        if (friendRequestRepository.existsBySenderIdAndReceiverId(sender.getId(), receiver.getId())
                || friendRequestRepository.existsBySenderIdAndReceiverId(receiver.getId(), sender.getId())) {
            throw new IllegalArgumentException("친구 요청이 이미 존재합니다.");
        }
        FriendRequest fr = friendRequestRepository.save(new FriendRequest(sender.getId(), receiver.getId()));
        return toFriendRequestResponse(fr, sender, receiver);
    }

    @Transactional
    public void accept(UUID requestId, UUID userId) {
        FriendRequest fr = friendRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("이미 처리된 요청입니다."));
        UUID senderId = fr.getSenderId();
        UUID receiverId = fr.getReceiverId();
        if (!receiverId.equals(userId)) throw new AccessDeniedException("친구 요청 수락 권한이 없습니다.");

        Set<UUID> ids = Set.of(senderId, receiverId);
        Map<UUID, User> users = userRepository.findAllByIds(ids).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        User sender = Optional.ofNullable(users.get(senderId)).orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다: " + senderId));
        User receiver = Optional.ofNullable(users.get(receiverId)).orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다: " + receiverId));

        if (!sender.isFriend(receiverId)) {
            sender.addFriend(receiverId);
            userRepository.save(sender);
        }
        if (!receiver.isFriend(senderId)) {
            receiver.addFriend(senderId);
            userRepository.save(receiver);
        }

        friendRequestRepository.hardDeleteById(requestId);
        friendRequestRepository.softDeleteBySenderAndReceiver(receiverId, senderId);
    }

    @Transactional
    public void reject(UUID requestId, UUID userId) {
        FriendRequest fr = friendRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("이미 처리된 요청입니다."));
        if (!fr.getReceiverId().equals(userId)) throw new AccessDeniedException("친구 요청 거절 권한이 없습니다.");
        friendRequestRepository.hardDeleteById(requestId);
    }

    @Transactional
    public void cancel(UUID requestId, UUID userId) {
        FriendRequest fr = friendRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("이미 처리된 요청입니다."));
        if (!fr.getSenderId().equals(userId)) throw new AccessDeniedException("친구 요청 거절 권한이 없습니다.");
        friendRequestRepository.softDeleteById(requestId);
    }

    public void clearFriendRequests(UUID userId) {
        friendRequestRepository.softDeleteAllByUserId(userId);
    }
}
