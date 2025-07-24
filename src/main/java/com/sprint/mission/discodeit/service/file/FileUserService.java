package com.sprint.mission.discodeit.service.file;

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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.FileService;
import com.sprint.mission.discodeit.service.UserService;

public class FileUserService implements UserService, FileService {
	private static final String DATA_DIR = "data/";
	private static final String USERS_FILE = DATA_DIR + "users";
	private static final String LOGIN_MAPPING_FILE = DATA_DIR + "loginMapping";

	private final Map<UUID, User> userMap;
	private final Map<String, UUID> loginIdToUUID;

	public FileUserService() {
		userMap = new ConcurrentHashMap<>();
		loginIdToUUID = new ConcurrentHashMap<>();

		createDirectoryIfNotExists();
		loadFile(USERS_FILE, userMap);
		loadFile(LOGIN_MAPPING_FILE, loginIdToUUID);
	}

	@Override
	public User login(String loginId, String password) {
		if (loginId == null || password == null) return null;
		if (loginIdToUUID.containsKey(loginId)) {
			User user = userMap.get(loginIdToUUID.get(loginId));
			if (user.getPassword().equals(password)) {
				return user;
			} else {
				return null;
			}
		}
		return null;
	}

	@Override
	public User createUser(String loginId, String password, String defaultNickname) {
		if (existsLoginId(loginId)) return null;

		User user = new User(loginId, password, defaultNickname);
		userMap.put(user.getId(), user);
		loginIdToUUID.put(loginId, user.getId());

		saveFile(USERS_FILE, userMap);
		saveFile(LOGIN_MAPPING_FILE, loginIdToUUID);
		return user;
	}

	@Override
	public User getUserById(UUID id) {
		return userMap.get(id);
	}

	@Override
	public User getUserByLoginId(String loginId) {
		return userMap.get(loginIdToUUID.get(loginId));
	}

	@Override
	public List<User> getAllUsers() {
		List<User> userList = new ArrayList<>(userMap.values());
		userList.sort((u1, u2) -> u1.getDefaultNickname().compareTo(u2.getDefaultNickname()));
		return userList;
	}

	@Override
	public boolean updateUserPassword(UUID id, String password) {
		if (id == null || password == null) return false;
		if (!userMap.containsKey(id)) return false;

		User user = userMap.get(id);
		user.updatePassword(password);
		user.updateUpdatedAt();

		saveFile(USERS_FILE, userMap);
		saveFile(LOGIN_MAPPING_FILE, loginIdToUUID);

		return true;
	}

	@Override
	public boolean deleteUser(UUID id) {
		if (id == null || !userMap.containsKey(id)) return false;

		User user = userMap.get(id);
		loginIdToUUID.remove(user.getLoginId());
		userMap.remove(id);

		saveFile(USERS_FILE, userMap);
		saveFile(LOGIN_MAPPING_FILE, loginIdToUUID);

		return true;
	}

	@Override
	public boolean deleteUser(String loginId) {
		if (loginId == null || !loginIdToUUID.containsKey(loginId)) return false;

		UUID userId = loginIdToUUID.get(loginId);
		userMap.remove(userId);
		loginIdToUUID.remove(loginId);

		saveFile(USERS_FILE, userMap);
		saveFile(LOGIN_MAPPING_FILE, loginIdToUUID);

		return true;
	}

	public boolean existsLoginId(String loginId) {
		if (loginId == null) return true;
		return loginIdToUUID.containsKey(loginId);
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
