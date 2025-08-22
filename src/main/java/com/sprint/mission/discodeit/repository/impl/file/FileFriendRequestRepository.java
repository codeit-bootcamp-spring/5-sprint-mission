package com.sprint.mission.discodeit.repository.impl.file;

import com.sprint.mission.discodeit.config.AppProperties;
import com.sprint.mission.discodeit.domain.entity.FriendRequest;
import com.sprint.mission.discodeit.repository.FriendRequestRepository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@Profile("dev")
public class FileFriendRequestRepository extends AbstractFileRepository<FriendRequest> implements
    FriendRequestRepository {

  public FileFriendRequestRepository(AppProperties appProperties) {
    super(FriendRequest.class, appProperties.storage());
  }

  @Override
  public boolean existsBySenderIdAndReceiverId(UUID senderId, UUID receiverId) {
    Objects.requireNonNull(senderId, "senderId must not be null");
    Objects.requireNonNull(receiverId, "receiverId must not be null");
    if (senderId.equals(receiverId)) {
      return false;
    }
    return findAll().stream()
        .anyMatch(fr -> senderId.equals(fr.getSenderId()) && receiverId.equals(fr.getReceiverId()));
  }

  @Override
  public boolean existsBetween(UUID userA, UUID userB) {
    Objects.requireNonNull(userA, "userA must not be null");
    Objects.requireNonNull(userB, "userB must not be null");
    if (userA.equals(userB)) {
      return false;
    }
    return findAll().stream().anyMatch(fr ->
        (userA.equals(fr.getSenderId()) && userB.equals(fr.getReceiverId()))
            || (userB.equals(fr.getSenderId()) && userA.equals(fr.getReceiverId()))
    );
  }

  @Override
  public List<FriendRequest> findAllBySenderId(UUID senderId) {
    Objects.requireNonNull(senderId, "senderId must not be null");
    return findAll().stream()
        .filter(fr -> senderId.equals(fr.getSenderId()))
        .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
        .toList();
  }

  @Override
  public List<FriendRequest> findAllByReceiverId(UUID receiverId) {
    Objects.requireNonNull(receiverId, "receiverId must not be null");
    return findAll().stream()
        .filter(fr -> receiverId.equals(fr.getReceiverId()))
        .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
        .toList();
  }

  @Override
  public void softDeleteAllByUserId(UUID userId) {
    Objects.requireNonNull(userId, "userId must not be null");

    try (Stream<Path> s = streamSerializedFiles()) {
      s.map(this::readObject).flatMap(Optional::stream)
          .filter(FriendRequest::isNotDeleted)
          .filter(fr -> userId.equals(fr.getSenderId()) || userId.equals(fr.getReceiverId()))
          .forEach(FriendRequest::delete);
    } catch (IOException e) {
      log.warn("Failed to list saved files: {}", directory, e);
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
  }

  @Override
  public boolean softDeleteBySenderAndReceiver(UUID senderId, UUID receiverId) {
    Objects.requireNonNull(senderId, "senderId must not be null");
    Objects.requireNonNull(receiverId, "receiverId must not be null");
    Optional<FriendRequest> fr = findAll().stream()
        .filter(e -> senderId.equals(e.getSenderId()) && receiverId.equals(e.getReceiverId()))
        .findFirst();
    return fr.map(e -> softDeleteById(e.getId())).orElse(false);
  }

  @Override
  public boolean hardDeleteBySenderAndReceiver(UUID senderId, UUID receiverId) {
    Objects.requireNonNull(senderId, "senderId must not be null");
    Objects.requireNonNull(receiverId, "receiverId must not be null");
    Optional<FriendRequest> fr = findAll().stream()
        .filter(e -> senderId.equals(e.getSenderId()) && receiverId.equals(e.getReceiverId()))
        .findFirst();
    return fr.map(e -> hardDeleteById(e.getId())).orElse(false);
  }
}
