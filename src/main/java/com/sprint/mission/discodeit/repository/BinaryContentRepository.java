package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "local")
public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {
}
