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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileReadStatusRepository implements ReadStatusRepository {
	private final String DATA_DIR;
	private final String EXTENSION = ".ser";
	private final String READ_STATUS_FILE;
	private final String USER_INDEX_FILE;
	private final String CHANNEL_INDEX_FILE;

	private final Map<UUID, ReadStatus> readStatusMap;
	private final Map<UUID, List<UUID>> userToReadStatusMap;
	private final Map<UUID, List<UUID>> channelToReadStatusMap;

	public FileReadStatusRepository(@Value("${discodeit.repository.file-directory:.discodeit}") String fileDirectory) {
		readStatusMap = new ConcurrentHashMap<>();
		userToReadStatusMap = new ConcurrentHashMap<>();
		channelToReadStatusMap = new ConcurrentHashMap<>();

		this.DATA_DIR = fileDirectory.endsWith("/") ? fileDirectory : fileDirectory + "/";
		this.READ_STATUS_FILE = DATA_DIR + "readStatus" + EXTENSION;
		this.USER_INDEX_FILE = DATA_DIR + "readStatusUserIndex" + EXTENSION;
		this.CHANNEL_INDEX_FILE = DATA_DIR + "readStatusChannelIndex" + EXTENSION;

		createDirectoryIfNotExists();
		loadFile();
	}

	@Override
	public void save(ReadStatus status) {
		if (status == null || status.getId() == null) {
			return;
		}

		readStatusMap.put(status.getId(), status);

		List<UUID> userReadStatusIds = userToReadStatusMap.computeIfAbsent(status.getUserId(), k -> new ArrayList<>());
		if (!userReadStatusIds.contains(status.getId())) {
			userReadStatusIds.add(status.getId());
		}

		List<UUID> channelReadStatusIds = channelToReadStatusMap.computeIfAbsent(status.getChannelId(), k -> new ArrayList<>());
		if (!channelReadStatusIds.contains(status.getId())) {
			channelReadStatusIds.add(status.getId());
		}

		saveFile();
	}

	@Override
	public Optional<ReadStatus> findById(UUID id) {
		return Optional.ofNullable(readStatusMap.get(id))
			.map(ReadStatus::copy);
	}

	@Override
	public List<ReadStatus> findByUserId(UUID userId) {
		if (userId == null) {
			return List.of();
		}

		List<UUID> readStatusIds = userToReadStatusMap.get(userId);
		if (readStatusIds == null) {
			return List.of();
		}

		return readStatusIds.stream()
			.map(readStatusMap::get)
			.filter(rs -> rs != null)
			.map(ReadStatus::copy)
			.toList();
	}

	@Override
	public List<ReadStatus> findByChannelId(UUID channelId) {
		if (channelId == null) {
			return List.of();
		}

		List<UUID> readStatusIds = channelToReadStatusMap.get(channelId);
		if (readStatusIds == null) {
			return List.of();
		}

		return readStatusIds.stream()
			.map(readStatusMap::get)
			.filter(rs -> rs != null)
			.map(ReadStatus::copy)
			.toList();
	}

	@Override
	public List<ReadStatus> findByChannelIdAndUserId(UUID channelId, UUID userId) {
		if (channelId == null || userId == null) {
			return List.of();
		}

		List<UUID> channelIds = channelToReadStatusMap.getOrDefault(channelId, List.of());
		List<UUID> userIds = userToReadStatusMap.getOrDefault(userId, List.of());

		return channelIds.stream()
			.filter(userIds::contains)
			.map(readStatusMap::get)
			.filter(Objects::nonNull)
			.map(ReadStatus::copy)
			.toList();
	}

	@Override
	public List<ReadStatus> findAll() {
		return readStatusMap.values().stream()
			.map(ReadStatus::copy)
			.toList();
	}

	@Override
	public void deleteById(UUID id) {
		if (id == null) {
			return;
		}

		ReadStatus readStatus = readStatusMap.get(id);
		if (readStatus != null) {
			removeFromMaps(id, readStatus);
			saveFile();
		}
	}

	@Override
	public void deleteByChannelId(UUID channelId) {
		if (channelId == null) {
			return;
		}

		List<UUID> readStatusIds = channelToReadStatusMap.get(channelId);
		if (readStatusIds != null) {
			List<UUID> idsToDelete = new ArrayList<>(readStatusIds);

			for (UUID id : idsToDelete) {
				ReadStatus readStatus = readStatusMap.get(id);
				if (readStatus != null) {
					removeFromMaps(id, readStatus);
				}
			}

			saveFile();
		}
	}

	@Override
	public void deleteByUserIdAndChannelId(UUID userId, UUID channelId) {
		if (userId == null || channelId == null) {
			return;
		}
		List<ReadStatus> toDelete = findByChannelIdAndUserId(channelId, userId);
		for (ReadStatus rs : toDelete) {
			deleteById(rs.getId());
		}

	}

	@Override
	public boolean existsByChannelIdAndUserId(UUID channelId, UUID userId) {
		if (channelId == null || userId == null) {
			return false;
		}


		List<UUID> channelReadStatusIds = channelToReadStatusMap.get(channelId);
		if (channelReadStatusIds == null) {
			return false;
		}


		for (UUID readStatusId : channelReadStatusIds) {
			ReadStatus rs = readStatusMap.get(readStatusId);
			if (rs != null && rs.getUserId().equals(userId)) {
				return true;
			}
		}

		return false;
	}

	private void removeFromMaps(UUID id, ReadStatus readStatus) {
		readStatusMap.remove(id);
		removeFromUserIndex(id, readStatus.getUserId());
		removeFromChannelIndex(id, readStatus.getChannelId());
	}

	private void removeFromUserIndex(UUID id, UUID userId) {
		List<UUID> userReadStatusIds = userToReadStatusMap.get(userId);
		if (userReadStatusIds != null) {
			userReadStatusIds.remove(id);
			if (userReadStatusIds.isEmpty()) {
				userToReadStatusMap.remove(userId);
			}
		}
	}

	private void removeFromChannelIndex(UUID id, UUID channelId) {
		List<UUID> channelReadStatusIds = channelToReadStatusMap.get(channelId);
		if (channelReadStatusIds != null) {
			channelReadStatusIds.remove(id);
			if (channelReadStatusIds.isEmpty()) {
				channelToReadStatusMap.remove(channelId);
			}
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
		Map<UUID, ReadStatus> tempReadStatusMap = null;
		Map<UUID, List<UUID>> tempUserIndex = null;
		Map<UUID, List<UUID>> tempChannelIndex = null;
		boolean readStatusLoaded = false;
		boolean userIndexLoaded = false;
		boolean channelIndexLoaded = false;

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(READ_STATUS_FILE))) {
			tempReadStatusMap = (Map<UUID, ReadStatus>) ois.readObject();
			readStatusLoaded = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_INDEX_FILE))) {
			tempUserIndex = (Map<UUID, List<UUID>>) ois.readObject();
			userIndexLoaded = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CHANNEL_INDEX_FILE))) {
			tempChannelIndex = (Map<UUID, List<UUID>>) ois.readObject();
			channelIndexLoaded = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (readStatusLoaded && userIndexLoaded && channelIndexLoaded) {
			readStatusMap.clear();
			readStatusMap.putAll(tempReadStatusMap);
			userToReadStatusMap.clear();
			userToReadStatusMap.putAll(tempUserIndex);
			channelToReadStatusMap.clear();
			channelToReadStatusMap.putAll(tempChannelIndex);
		}
	}

	@Override
	public void saveFile() {
		Path readStatusTmp = Paths.get(READ_STATUS_FILE + ".tmp");
		Path userIndexTmp = Paths.get(USER_INDEX_FILE + ".tmp");
		Path channelIndexTmp = Paths.get(CHANNEL_INDEX_FILE + ".tmp");

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(readStatusTmp.toFile()))) {
			oos.writeObject(readStatusMap);
		} catch (Exception e) {
			throw new RuntimeException("ReadStatus 저장 파일 쓰기 실패", e);
		}

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userIndexTmp.toFile()))) {
			oos.writeObject(userToReadStatusMap);
		} catch (Exception e) {
			throw new RuntimeException("ReadStatus 사용자 인덱스 파일 쓰기 실패", e);
		}

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(channelIndexTmp.toFile()))) {
			oos.writeObject(channelToReadStatusMap);
		} catch (Exception e) {
			throw new RuntimeException("ReadStatus 채널 인덱스 파일 쓰기 실패", e);
		}

		try {
			Files.move(readStatusTmp, Paths.get(READ_STATUS_FILE), StandardCopyOption.REPLACE_EXISTING);
			Files.move(userIndexTmp, Paths.get(USER_INDEX_FILE), StandardCopyOption.REPLACE_EXISTING);
			Files.move(channelIndexTmp, Paths.get(CHANNEL_INDEX_FILE), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("임시 파일 이동 실패", e);
		}

		try {
			Files.deleteIfExists(readStatusTmp);
			Files.deleteIfExists(userIndexTmp);
			Files.deleteIfExists(channelIndexTmp);
		} catch (IOException e) {
			throw new RuntimeException("임시 파일 삭제 실패", e);
		}
	}
}