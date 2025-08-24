package com.sprint.mission.discodeit.repository.impl.file;

import com.sprint.mission.discodeit.config.AppProperties;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.enums.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@Profile("dev")
public class FileChannelRepository extends AbstractFileRepository<Channel> implements
    ChannelRepository {

  public FileChannelRepository(AppProperties appProperties) {
    super(Channel.class, appProperties.storage());
  }

  @Override
  public List<Channel> findAllPublic() {
    try (Stream<Path> s = streamSerializedFiles()) {
      return s.map(this::readObject).flatMap(Optional::stream)
          .filter(Channel::isNotDeleted)
          .filter(c -> c.getType() == ChannelType.PUBLIC)
          .toList();
    } catch (IOException e) {
      log.warn("Failed to list saved files: {}", directory, e);
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
  }

  @Override
  public boolean existsBetween(UUID userId1, UUID userId2) {
    if (userId1 == null || userId2 == null || userId1.equals(userId2)) {
      return false;
    }

    Set<UUID> pair = Set.of(userId1, userId2);

    try (Stream<Path> s = streamSerializedFiles()) {
      return s.map(this::readObject).flatMap(Optional::stream)
          .anyMatch(c ->
              c.isNotDeleted()
                  && c.getType() == ChannelType.PRIVATE
                  && c.getParticipantIds().size() == 2
                  && c.getParticipantIds().containsAll(pair)
          );
    } catch (IOException e) {
      log.warn("Failed to list saved files: {}", directory, e);
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
  }
}
