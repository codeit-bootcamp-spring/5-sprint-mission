package com.sprint.mission.discodeit.repository.jcf;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

public class JCFBinaryContentRepository implements BinaryContentRepository {
	private final Map<UUID, BinaryContent> binaryContentMap;

	public JCFBinaryContentRepository() {
		binaryContentMap = new ConcurrentHashMap<>();
	}

	@Override
	public void save(BinaryContent binaryContent) {
		if (binaryContent == null || binaryContent.getId() == null) {
			return;
		}
		binaryContentMap.put(binaryContent.getId(), binaryContent);
	}

	@Override
	public Optional<BinaryContent> findById(UUID id) {
		return Optional.ofNullable(binaryContentMap.get(id)).map(BinaryContent::copy);
	}

	@Override
	public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
		if (ids == null || ids.isEmpty()) {
			return new ArrayList<>();
		}

		return ids.stream()
			.map(binaryContentMap::get)
			.filter(Objects::nonNull)
			.map(BinaryContent::copy)
			.toList();
	}

	@Override
	public List<BinaryContent> findAll() {
		List<BinaryContent> bcList = new ArrayList<>();
		for (BinaryContent binaryContent : binaryContentMap.values()) {
			bcList.add(binaryContent.copy());
		}
		return bcList.stream().sorted(Comparator.comparing(BinaryContent::getCreatedAt)).toList();
	}

	@Override
	public void deleteById(UUID id) {
		if (id == null) return;
		binaryContentMap.remove(id);
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