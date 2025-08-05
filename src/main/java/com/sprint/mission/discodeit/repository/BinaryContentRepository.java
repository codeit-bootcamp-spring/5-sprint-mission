package com.sprint.mission.discodeit.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.BinaryContent;

public interface BinaryContentRepository {
	void save(BinaryContent binaryContent);
	Optional<BinaryContent> findById(UUID id);
	List<BinaryContent> findAll();
	void deleteById(UUID ID);

	void createDirectoryIfNotExists();
	void loadFile();
	void saveFile();
}
