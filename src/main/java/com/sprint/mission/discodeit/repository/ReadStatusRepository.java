package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    List<ReadStatusDto> findAllByUserId(UUID userId);

    default ReadStatus getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "ReadStatus with id %s not found".formatted(id))
        );
    }

    void deleteAllByChannel(Channel channel);

    List<ReadStatus> findAllByChannelIn(Collection<Channel> channels);
}
