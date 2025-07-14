package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

public class JCFUserService implements UserService {
	private final Map<UUID, User> UserMap = new ConcurrentHashMap<>();
	private final Map<String, UUID> loginIdToUUID = new ConcurrentHashMap<>();

	@Override
	public User createUser(String loginId, String password, String defaultNickname) {
		if ( loginIdToUUID.containsKey(loginId) ) return null;

		User user = new User(loginId, password, defaultNickname);
		UserMap.put(user.getId(), user);
		loginIdToUUID.put(loginId, user.getId());

		return user;
	}

	@Override
	public User getUser(UUID id) {
		return UserMap.get(id);
	}

	@Override
	public User getUser(String loginId) {
		return UserMap.get(loginIdToUUID.get(loginId));
	}

	@Override
	public List<User> getUserAll() {
		return new ArrayList<>(UserMap.values());
	}

	@Override
	public boolean updateUserPassword(UUID id, String password) {
		if (id == null || password == null) return false;
		UserMap.get(id).updatePassword(password);

		return true;
	}

	@Override
	public boolean deleteUser(UUID id) {
		if (id == null || !UserMap.containsKey(id)) return false;

		loginIdToUUID.remove(UserMap.get(id).getLoginId());
		UserMap.remove(id);


		return true;
	}

	@Override
	public boolean deleteUser(String loginId){
		if ( loginId == null || !loginIdToUUID.containsKey(loginId)) return false;

		UserMap.remove(loginIdToUUID.get(loginId));
		loginIdToUUID.remove(loginId);

		return true;
	}
}
