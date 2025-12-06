package com.sprint.mission.discodeit.binarycontent.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

}
