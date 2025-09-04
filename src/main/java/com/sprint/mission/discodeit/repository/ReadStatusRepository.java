package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    List<ReadStatus> findAllByUser(User user);

    List<ReadStatus> findAllByChannel(Channel channel);

    void deleteAllByChannel(Channel channel);

    boolean existsByUserAndChannel(User user, Channel channel);
}
