package com.sprint.mission.discodeit.repository.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
@RequiredArgsConstructor
public class JCFUserRepository implements UserRepository {
	private final Map<UUID, User> userMap;
	private final Map<String, UUID> loginIdToUUID;
	private final Map<String, UUID> emailToUUID;

	public JCFUserRepository() {
		userMap = new ConcurrentHashMap<>();
		loginIdToUUID = new ConcurrentHashMap<>();
		emailToUUID = new ConcurrentHashMap<>();
	}

	@Override
	public void save(User user) {
		if (user == null || user.getId() == null) {
			return;
		}

		userMap.put(user.getId(), user);
		loginIdToUUID.put(user.getUsername(), user.getId());
	}

	@Override
	public Optional<User> findById(UUID id) {
		return Optional.ofNullable(userMap.get(id)).map(User::copy);
	}

	@Override
	public Optional<User> findByLoginId(String loginId) {
		UUID id = loginIdToUUID.get(loginId);
		if(id == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(userMap.get(id)).map(User::copy);
	}

	@Override
	public Optional<User> findByLoginIdAnaPassword(String loginId, String password) {
		return Optional.empty();
	}

	@Override
	public List<User> findAll() {
		List<User> userList = new ArrayList<>();
		for (User user : userMap.values()) {
			userList.add(user.copy());
		}

		return userList;
	}

	@Override
	public void deleteById(UUID id) {
		if (id == null) {
			return;
		}

		User user = userMap.get(id);
		if (user != null) {
			loginIdToUUID.remove(user.getUsername());
			userMap.remove(id);
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
		}
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

	@Override
	public boolean existsById(UUID id) {
		if (id == null) return false;
		return userMap.containsKey(id);
	}

	@Override
	public boolean existsByLoginId(String loginId) {
		if (loginId == null) return false;
		return loginIdToUUID.containsKey(loginId);
	}

	@Override
	public boolean existsByEmail(String email) {
		if (email == null) return false;
		return emailToUUID.containsKey(email);
	}
}
