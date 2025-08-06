package com.sprint.mission.discodeit.repository.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {
	private final String DATA_DIR = "data/";
	private final String EXTENSION = ".ser";
	private final String BINARY_CONTENT_FILE = DATA_DIR + "binaryContent" + EXTENSION;

	private final Map<UUID, BinaryContent> binaryContentMap;

	public FileBinaryContentRepository() {
		binaryContentMap = new ConcurrentHashMap<>();

		createDirectoryIfNotExists();
		loadFile();
	}

	@Override
	public void save(BinaryContent binaryContent) {
		if (binaryContent == null || binaryContent.getId() == null) {
			return;
		}

		binaryContentMap.put(binaryContent.getId(), binaryContent);
		saveFile();
	}

	@Override
	public Optional<BinaryContent> findById(UUID id) {
		return Optional.ofNullable(binaryContentMap.get(id)).map(BinaryContent::copy);
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
		BinaryContent bc = binaryContentMap.get(id);

		if (bc != null) {
			binaryContentMap.remove(id);

			saveFile();
		}
	}

	@Override
	public void createDirectoryIfNotExists() {
		try {
			Path dataPath = Paths.get(DATA_DIR);
			if (!Files.exists(dataPath)) {
				Files.createDirectories(dataPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadFile() {
		Map<UUID, BinaryContent> tempBinaryContentMap = null;
		boolean BinaryContentMapLoaded = false;

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BINARY_CONTENT_FILE))) {
			tempBinaryContentMap = (Map<UUID, BinaryContent>)ois.readObject();
			BinaryContentMapLoaded = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (BinaryContentMapLoaded) {
			binaryContentMap.clear();
			binaryContentMap.putAll(tempBinaryContentMap);
		}
	}

	@Override
	public void saveFile() {
		Path tempBinaryContentMap = Paths.get(BINARY_CONTENT_FILE + ".tmp");

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempBinaryContentMap.toFile()))) {
			oos.writeObject(binaryContentMap);
		} catch (Exception e) {
			throw new RuntimeException("유저 저장 파일 읽기 실패", e);
		}

		try {
			Files.move(tempBinaryContentMap, Paths.get(BINARY_CONTENT_FILE), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("임시 파일 이동 실패", e);
		}

		try {
			Files.deleteIfExists(tempBinaryContentMap);
		} catch (IOException e) {
			throw new RuntimeException("임시 파일 삭제 실패", e);
		}
	}
}
