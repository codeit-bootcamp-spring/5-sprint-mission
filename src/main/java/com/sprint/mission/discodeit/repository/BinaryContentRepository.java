package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;

public interface BinaryContentRepository {
	public BinaryContent save(BinaryContent binaryContent);

	public List<BinaryContent> saveAll(List<BinaryContent> binaryContents);

	public Optional<BinaryContent> find(UUID id);

	public List<BinaryContent> findAll();

	public List<BinaryContent> findAllByIdIn(List<UUID> ids);

	public void delete(UUID id);

	boolean isEmpty(UUID id);

	void deleteAll();
}
