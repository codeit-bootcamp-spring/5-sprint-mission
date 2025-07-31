package com.sprint.mission.discodeit.repository.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

public class JCFUserRepository implements UserRepository {
	private final Map<UUID, User> UserMap;
	private final Map<String, UUID> loginIdToUUID;

	public JCFUserRepository() {
		UserMap = new ConcurrentHashMap<>();
		loginIdToUUID = new ConcurrentHashMap<>();
	}

	@Override
	public void save(User user) {
		if (user == null) {
			throw new IllegalArgumentException("null!!");
		}
		if (user.getId() == null) {
			throw new IllegalArgumentException("null!!");
		}

		UserMap.put(user.getId(), user);
		loginIdToUUID.put(user.getLoginId(), user.getId());

	}

	@Override
	public User findById(UUID id) {
		if (id == null) {
			throw new IllegalArgumentException("null!!");
		}

		return UserMap.get(id);
	}

	@Override
	public User findByLoginId(String loginId) {
		if (loginId == null) {
			throw new IllegalArgumentException("null!!");
		}

		return UserMap.get(loginIdToUUID.get(loginId));
	}

	@Override
	public List<User> findAll() {
		List<User> userList = new ArrayList<>(UserMap.values());
		userList.sort((u1, u2) -> u1.getDefaultNickname().compareTo(u2.getDefaultNickname()));
		return userList;
	}

	@Override
	public void deleteById(UUID id) {
		if (id == null) {
			throw new IllegalArgumentException("null!!");
		}

		User user = UserMap.get(id);
		if (user != null) {
			loginIdToUUID.remove(user.getLoginId());
			UserMap.remove(id);
		}
	}

	@Override
	public void deleteByLoginId(String loginId){
		if (loginId == null) {
			throw new IllegalArgumentException("null!!");
		}

		UUID userId = loginIdToUUID.get(loginId);
		if (userId != null) {
			UserMap.remove(userId);
			loginIdToUUID.remove(loginId);
		}
	}

	public boolean isExistLoginId(String loginId) {
		if (loginId == null) return true;
		return loginIdToUUID.containsKey(loginId);
	}
}
