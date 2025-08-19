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

import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

@Repository
public class FileUserRepository implements UserRepository {
	private final String DATA_DIR = "data/";
	private final String EXTENSION = ".ser";
	private final String USERS_FILE = DATA_DIR + "users" + EXTENSION;
	private final String LOGIN_MAPPING_FILE = DATA_DIR + "loginMapping" + EXTENSION;
	private final String EMAIL_MAPPING_FILE = DATA_DIR + "emailMapping" + EXTENSION;

	private final Map<UUID, User> userMap;
	private final Map<String, UUID> loginIdToUUID;
	private final Map<String, UUID> emailToUUID;

	public FileUserRepository() {
		userMap = new ConcurrentHashMap<>();
		loginIdToUUID = new ConcurrentHashMap<>();
		emailToUUID = new ConcurrentHashMap<>();

		createDirectoryIfNotExists();
		loadFile();

	}

	@Override
	public void save(User user) {
		if (user == null || user.getId() == null) {
			return;
		}

		userMap.put(user.getId(), user);
		loginIdToUUID.put(user.getLoginId(), user.getId());
		emailToUUID.put(user.getEmail(), user.getId());

		saveFile();
	}

	@Override
	public Optional<User> findById(UUID id) {
		return Optional.ofNullable(userMap.get(id)).map(User::copy);
	}

	@Override
	public Optional<User> findByLoginId(String loginId) {
		UUID userId = loginIdToUUID.get(loginId);
		if (userId == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(userMap.get(userId)).map(User::copy);
	}

	@Override
	public Optional<User> findByLoginIdAnaPassword(String loginId, String password) {
		if (loginId == null || password == null) {
			return Optional.empty();
		}

		UUID userId = loginIdToUUID.get(loginId);
		if (userId == null) {
			return Optional.empty();
		}

		User user = userMap.get(userId);
		if (user != null && user.getPassword().equals(password)) {
			return Optional.of(user.copy());
		}
		return Optional.empty();
	}

	@Override
	public List<User> findAll() {
		List<User> userList = new ArrayList<>();
		for (User user : userMap.values()) {
			userList.add(user.copy());
		}
		return userList.stream().sorted(Comparator.comparing(User::getDefaultNickname)).toList();
	}

	@Override
	public void deleteById(UUID id) {
		if (id == null) {
			return;
		}

		User user = userMap.get(id);
		if (user != null) {
			loginIdToUUID.remove(user.getLoginId());
			userMap.remove(id);

			saveFile();
		}
	}

	@Override
	public void deleteByLoginId(String loginId) {
		if (loginId == null) {
			return;
		}

		UUID userId = loginIdToUUID.get(loginId);
		if (userId != null) {
			userMap.remove(userId);
			loginIdToUUID.remove(loginId);

			saveFile();
		}
	}

	@Override
	public boolean existsById(UUID id) {
		if (id == null)
			return false;
		return userMap.containsKey(id);
	}

	@Override
	public boolean existsByLoginId(String loginId) {
		if (loginId == null)
			return true;
		return loginIdToUUID.containsKey(loginId);
	}

	@Override
	public boolean existsByEmail(String email) {
		if (email == null)
			return false;
		return emailToUUID.containsKey(email);
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
		Map<UUID, User> tempUserMap = null;
		Map<String, UUID> tempLoginIdToUUID = null;
		Map<String, UUID> tempEmailToUUID = null;
		boolean channelsLoaded = false;
		boolean mappingLoaded = false;
		boolean emailMappingLoaded = false;

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
			tempUserMap = (Map<UUID, User>)ois.readObject();
			channelsLoaded = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(LOGIN_MAPPING_FILE))) {
			tempLoginIdToUUID = (Map<String, UUID>)ois.readObject();
			mappingLoaded = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(EMAIL_MAPPING_FILE))) {
			tempEmailToUUID = (Map<String, UUID>)ois.readObject();
			emailMappingLoaded = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (channelsLoaded && mappingLoaded && emailMappingLoaded) {
			userMap.clear();
			userMap.putAll(tempUserMap);
			loginIdToUUID.clear();
			loginIdToUUID.putAll(tempLoginIdToUUID);
			emailToUUID.clear();
			emailToUUID.putAll(tempEmailToUUID);
		}
	}

	@Override
	public void saveFile() {

		Path usersTmp = Paths.get(USERS_FILE + ".tmp");
		Path loginMappingTmp = Paths.get(LOGIN_MAPPING_FILE + ".tmp");
		Path emailMappingTmp = Paths.get(EMAIL_MAPPING_FILE + ".tmp");

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(usersTmp.toFile()))) {
			oos.writeObject(userMap);
		} catch (Exception e) {
			throw new RuntimeException("유저 저장 파일 읽기 실패", e);
		}

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(loginMappingTmp.toFile()))) {
			oos.writeObject(loginIdToUUID);
		} catch (Exception e) {
			throw new RuntimeException("유저 캐시 파일 읽기 실패", e);
		}

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(emailMappingTmp.toFile()))) {
			oos.writeObject(emailToUUID);
		} catch (Exception e) {
			throw new RuntimeException("유저 이메일 캐시 파일 읽기 실패", e);
		}

		try {
			Files.move(usersTmp, Paths.get(USERS_FILE), StandardCopyOption.REPLACE_EXISTING);
			Files.move(loginMappingTmp, Paths.get(LOGIN_MAPPING_FILE), StandardCopyOption.REPLACE_EXISTING);
			Files.move(emailMappingTmp, Paths.get(EMAIL_MAPPING_FILE), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("임시 파일 이동 실패", e);
		}

		try {
			Files.deleteIfExists(usersTmp);
			Files.deleteIfExists(loginMappingTmp);
			Files.deleteIfExists(emailMappingTmp);
		} catch (IOException e) {
			throw new RuntimeException("임시 파일 삭제 실패", e);
		}

	}

}
