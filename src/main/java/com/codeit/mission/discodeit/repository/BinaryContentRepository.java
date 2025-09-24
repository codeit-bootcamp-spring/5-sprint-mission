package com.codeit.mission.discodeit.repository;

import com.codeit.mission.discodeit.entity.BinaryContent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

}
