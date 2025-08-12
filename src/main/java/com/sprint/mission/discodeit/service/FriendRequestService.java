// package com.sprint.mission.discodeit.service;
//
// import com.sprint.mission.discodeit.domain.entity.FriendRequest;
// import com.sprint.mission.discodeit.domain.entity.User;
// import com.sprint.mission.discodeit.dto.response.FriendRequestResponse;
// import com.sprint.mission.discodeit.repository.FriendRequestRepository;
// import com.sprint.mission.discodeit.repository.UserRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.context.annotation.Profile;
// import org.springframework.stereotype.Service;
//
// import java.util.Comparator;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Map;
// import java.util.NoSuchElementException;
// import java.util.Set;
// import java.util.UUID;
// import java.util.function.Consumer;
// import java.util.function.Function;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;
//
// import static com.sprint.mission.discodeit.mapper.FriendRequestMapper.toFriendRequestResponse;
//
// @Service
// @RequiredArgsConstructor
// @Profile({"test", "dev"})
// public class FriendRequestService {
//
//     private final UserRepository userRepository;
//     private final FriendRequestRepository friendRequestRepository;
//
//     protected void update(UUID id, Consumer<FriendRequest> updater) {
//         FriendRequest entity = friendRequestRepository.getOrThrow(id);
//         updater.accept(entity);
//         friendRequestRepository.save(entity);
//     }
//
//     public List<FriendRequest> listSent(UUID userId) {
//         if (userId == null) return List.of();
//         return friendRequestRepository.getSentRequests(userId).stream()
//                 .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
//                 .toList();
//     }
//
//     public List<FriendRequest> listReceived(UUID userId) {
//         if (userId == null) return List.of();
//         return friendRequestRepository.getReceivedRequests(userId).stream()
//                 .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
//                 .toList();
//     }
//
//     public List<FriendRequest> listAllMine(UUID userId) {
//         if (userId == null) return List.of();
//         var seen = new HashSet<UUID>();
//         var sent = friendRequestRepository.getSentRequests(userId);
//         var received = friendRequestRepository.getReceivedRequests(userId);
//         return Stream.concat(sent.stream(), received.stream())
//                 .filter(fr -> seen.add(fr.getId()))
//                 .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
//                 .toList();
//     }
//
//     public List<FriendRequestResponse> getFriendRequests(UUID userId) {
//         if (userId == null) return List.of();
//
//         Set<UUID> seen = new HashSet<>();
//         List<FriendRequest> all = Stream.concat(
//                         friendRequestRepository.getSentRequests(userId).stream(),
//                         friendRequestRepository.getReceivedRequests(userId).stream()
//                 )
//                 .filter(fr -> fr != null && seen.add(fr.getId()))
//                 .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
//                 .toList();
//
//         if (all.isEmpty()) return List.of();
//
//         Set<UUID> ids = all.stream()
//                 .flatMap(fr -> Stream.of(fr.getSenderId(), fr.getReceiverId()))
//                 .collect(Collectors.toSet());
//
//         Map<UUID, User> userMap = userRepository.findAllByIds(ids).stream()
//                 .collect(Collectors.toMap(User::getId, Function.identity()));
//
//         return all.stream()
//                 .map(fr -> {
//                     User sender = userMap.get(fr.getSenderId());
//                     User receiver = userMap.get(fr.getReceiverId());
//                     if (sender == null || receiver == null) {
//                         throw new NoSuchElementException("요청에 필요한 사용자 정보를 찾을 수 없습니다: " + fr.getId());
//                     }
//                     return toFriendRequestResponse(sender, receiver);
//                 })
//                 .toList();
//     }
//
//
//     public FriendRequestResponse send(UUID senderId, String receiverUsername) {
//         userRepository.getOrThrow(senderId);
//         final User sender = userRepository.getOrThrow(senderId);
//         final User receiver = userRepository.findByUsername(receiverUsername).orElseThrow(
//                 () -> new NoSuchElementException("유저가 존재하지 않습니다."));
//         if (userRepository.getOrThrow(senderId).isFriend(receiver.getId()))
//             throw new IllegalArgumentException("이미 친구입니다.");
//         if (friendRequestRepository.existsBySenderIdAndReceiverId(senderId, receiver.getId())
//                 || friendRequestRepository.existsBySenderIdAndReceiverId(receiver.getId(), senderId))
//             throw new IllegalArgumentException("친구 요청이 이미 존재합니다.");
//         friendRequestRepository.save(new FriendRequest(senderId, receiver.getId()));
//         return toFriendRequestResponse(sender, receiver);
//     }
//
//     public void accept(UUID requestId) {
//         FriendRequest fr = friendRequestRepository.getOrThrow(requestId);
//         UUID senderId = fr.getSenderId();
//         UUID receiverId = fr.getReceiverId();
//         User sender = userRepository.getOrThrow(senderId);
//         User receiver = userRepository.getOrThrow(receiverId);
//         sender.addFriend(receiverId);
//         receiver.addFriend(senderId);
//         userRepository.save(sender);
//         userRepository.save(receiver);
//         friendRequestRepository.hardDeleteById(requestId);
//         friendRequestRepository.getSentRequests(receiverId).stream()
//                 .filter(x -> x.getReceiverId().equals(senderId))
//                 .map(FriendRequest::getId)
//                 .forEach(friendRequestRepository::hardDeleteById);
//     }
//
//     public void reject(UUID requestId) {
//         if (friendRequestRepository.findById(requestId).isEmpty()) throw new NoSuchElementException("이미 처리된 요청입니다.");
//         friendRequestRepository.hardDeleteById(requestId);
//     }
//
//     public void clear(UUID userId) {
//         if (userId == null) return;
//         friendRequestRepository.softDeleteAllByUserId(userId);
//     }
// }
