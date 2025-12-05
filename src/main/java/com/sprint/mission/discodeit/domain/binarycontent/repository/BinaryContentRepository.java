package com.sprint.mission.discodeit.domain.binarycontent.repository;

import com.sprint.mission.discodeit.domain.binarycontent.entity.BinaryContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

}
