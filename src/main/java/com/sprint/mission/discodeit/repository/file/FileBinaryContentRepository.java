package com.sprint.mission.discodeit.repository.file;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {
	private final String DATA_DIR = "data/";
	private final String EXTENSION = ".ser";
	private final String Binary_Content_FILE = DATA_DIR + "binaryContent" + EXTENSION;

	@Override
	public void save(BinaryContent binaryContent) {

	}

	@Override
	public Optional<BinaryContent> findById(UUID id) {
		return Optional.empty();
	}

	@Override
	public List<BinaryContent> findAll() {
		return List.of();
	}

	@Override
	public void deleteById(UUID ID) {

	}

	@Override
	public void createDirectoryIfNotExists() {

	}

	@Override
	public void loadFile() {

	}

	@Override
	public void saveFile() {

	}
}
