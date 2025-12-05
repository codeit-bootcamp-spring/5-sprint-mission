package com.sprint.mission.discodeit.infrastructrue.outbox.repository;

import com.sprint.mission.discodeit.infrastructrue.outbox.entity.OutboxEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findAllByOrderByCreatedAtAsc(Pageable pageable);
}
