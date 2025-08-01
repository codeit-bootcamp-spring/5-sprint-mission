package com.sprint.mission.discodeit.repository.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.FileRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Repository
public class FileUserRepository implements UserRepository, FileRepository {
	private static final String DATA_DIR = "data/";
	private static final String USERS_FILE = DATA_DIR + "users";
	private static final String LOGIN_MAPPING_FILE = DATA_DIR + "loginMapping";
	private static final String EMAIL_MAPPING_FILE = DATA_DIR + "emailMapping";

	private final Map<String, UUID> emailToUUID;
	private final Map<UUID, User> userMap;
	private final Map<String, UUID> loginIdToUUID;

	public FileUserRepository() {
		userMap = new ConcurrentHashMap<>();
		loginIdToUUID = new ConcurrentHashMap<>();
		emailToUUID = new ConcurrentHashMap<>();

		createDirectoryIfNotExists();
		loadFile(USERS_FILE, userMap);
		loadFile(LOGIN_MAPPING_FILE, loginIdToUUID);
		loadFile(EMAIL_MAPPING_FILE, emailToUUID);
	}

	@Override
	public void save(User user) {
		if (user == null || user.getId() == null) {
			return;
		}

		userMap.put(user.getId(), user);
		loginIdToUUID.put(user.getLoginId(), user.getId());

		saveFile(USERS_FILE, userMap);
		saveFile(LOGIN_MAPPING_FILE, loginIdToUUID);
		saveFile(EMAIL_MAPPING_FILE, emailToUUID);
	}

	@Override
	public Optional<User> findById(UUID id) {
		return Optional.ofNullable(userMap.get(id)).map(User::copy);
	}

	@Override
	public Optional<User> findByLoginId(String loginId) {
		UUID userId = loginIdToUUID.get(loginId);
		if(userId == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(userMap.get(userId)).map(User::copy);
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

			saveFile(USERS_FILE, userMap);
			saveFile(LOGIN_MAPPING_FILE, loginIdToUUID);
			saveFile(EMAIL_MAPPING_FILE, emailToUUID);
		}
	}

	@Override
	public void deleteByLoginId(String loginId){
		if (loginId == null) {
			return;
		}

		UUID userId = loginIdToUUID.get(loginId);
		if (userId != null) {
			userMap.remove(userId);
			loginIdToUUID.remove(loginId);

			saveFile(USERS_FILE, userMap);
			saveFile(LOGIN_MAPPING_FILE, loginIdToUUID);
			saveFile(EMAIL_MAPPING_FILE, emailToUUID);
		}
	}

	@Override
	public boolean existsByLoginId(String loginId) {
		if (loginId == null) return true;
		return loginIdToUUID.containsKey(loginId);
	}

	@Override
	public boolean existsByEmail(String email) {
		if (email == null) return false;
		return emailToUUID.containsKey(email);
	}

	@Override
	public void createDirectoryIfNotExists() {
		try{
			Path dataPath = Paths.get(DATA_DIR);
			if (!Files.exists(dataPath)) {
				Files.createDirectories(dataPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadFile(String filename, Map map) {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
			Map load = (Map) ois.readObject();
			map.putAll(load);
		} catch (FileNotFoundException ignored) {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveFile(String filename, Object data) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
			oos.writeObject(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
