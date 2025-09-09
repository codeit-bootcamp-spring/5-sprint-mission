package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

	public List<BinaryContent> findAllByIdIn(List<UUID> ids);

	void deleteByIdIn(List<UUID> ids);
}
