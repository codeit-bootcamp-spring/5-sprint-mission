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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {
	private final Map<UUID, UserStatus> userStatusMap;
	private final Map<UUID, UUID> userIdToStatusIdMap;
	private final String DATA_DIR = "data/";
	private final String EXTENSION = ".ser";
	private final String USER_STATUS_FILE = DATA_DIR + "userStatus" + EXTENSION;
	private final String USER_STATUS_MAPPING_FILE = DATA_DIR + "userStatusMapping" + EXTENSION;

	public FileUserStatusRepository() {
		userStatusMap = new ConcurrentHashMap<>();
		userIdToStatusIdMap = new ConcurrentHashMap<>();
		createDirectoryIfNotExists();
		loadFile();
	}

	@Override
	public void save(UserStatus status) {
		if (status == null || status.getId() == null) {
			return;
		}

		userStatusMap.put(status.getId(), status);
		saveFile();
	}

	@Override
	public Optional<UserStatus> findById(UUID id) {
		return Optional.ofNullable(userStatusMap.get(id)).map(UserStatus::copy);
	}

	@Override
	public Optional<UserStatus> findByUserId(UUID UserId) {
		UUID statusId = userIdToStatusIdMap.get(UserId);
		if (statusId == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(userStatusMap.get(statusId)).map(UserStatus::copy);
	}

	@Override
	public List<UserStatus> findByUserIdList(List<UUID> userIdList) {
		List<UserStatus> status = new ArrayList<>();
		for (UUID userId : userIdList) {
			findByUserId(userId).ifPresent(status::add);
		}

		return status;
	}

	@Override
	public List<UserStatus> findAll() {
		List<UserStatus> statusList = new ArrayList<>();
		for (UserStatus userStatus : userStatusMap.values()) {
			statusList.add(userStatus.copy());
		}
		return statusList;
	}

	@Override
	public void deleteById(UUID id) {
		if (id == null) {
			return;
		}

		UserStatus userStatus = userStatusMap.get(id);
		if (userStatus != null) {
			userStatusMap.remove(id);
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
		Map<UUID, UserStatus> tempUserStatusMap = null;
		Map<UUID, UUID> tempUserIdToStatusIdMap = null;
		boolean statusLoaded = false;
		boolean statusMappingLoaded = false;

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_STATUS_FILE))) {
			tempUserStatusMap = (Map<UUID, UserStatus>)ois.readObject();
			statusLoaded = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_STATUS_MAPPING_FILE))) {
			tempUserIdToStatusIdMap = (Map<UUID, UUID>)ois.readObject();
			statusMappingLoaded = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (statusLoaded && statusMappingLoaded) {
			userStatusMap.clear();
			userStatusMap.putAll(tempUserStatusMap);
			userIdToStatusIdMap.clear();
			userIdToStatusIdMap.putAll(tempUserIdToStatusIdMap);
		}
	}

	@Override
	public void saveFile() {
		Path userStatusMapTmp = Paths.get(USER_STATUS_FILE + ".tmp");
		Path userIdToStatusIdMapTmp = Paths.get(USER_STATUS_MAPPING_FILE + ".tmp");

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userStatusMapTmp.toFile()))) {
			oos.writeObject(userStatusMap);
		} catch (Exception e) {
			throw new RuntimeException("유저 저장 파일 읽기 실패", e);
		}

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userIdToStatusIdMapTmp.toFile()))) {
			oos.writeObject(userIdToStatusIdMap);
		} catch (Exception e) {
			throw new RuntimeException("유저 캐시 파일 읽기 실패", e);
		}


		try {
			Files.move(userStatusMapTmp, Paths.get(USER_STATUS_FILE), StandardCopyOption.REPLACE_EXISTING);
			Files.move(userIdToStatusIdMapTmp, Paths.get(USER_STATUS_MAPPING_FILE), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("임시 파일 이동 실패", e);
		}



		try {
			Files.deleteIfExists(userStatusMapTmp);
			Files.deleteIfExists(userIdToStatusIdMapTmp);
		} catch (IOException e) {
			throw new RuntimeException("임시 파일 삭제 실패", e);
		}
	}
}
