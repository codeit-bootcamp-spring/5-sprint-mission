package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

// 엔티티 타입과 그 엔티티의 PK타입을 넣어야함
public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {
}
