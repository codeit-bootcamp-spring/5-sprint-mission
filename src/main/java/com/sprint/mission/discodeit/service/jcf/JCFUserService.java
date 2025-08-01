package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

public class JCFUserService implements UserService {
	private final Map<UUID, User> UserMap;
	private final Map<String, UUID> loginIdToUUID;

	public JCFUserService() {
		UserMap = new ConcurrentHashMap<>();
		loginIdToUUID = new ConcurrentHashMap<>();
	}

	@Override
	public User createUser(UserCreateRequest request) {

		if (request.getLoginId() == null) return null;
		if ( isExistLoginId(request.getLoginId()) ) return null;

		User user = new User(request.toUser());
		UserMap.put(user.getId(), user);
		loginIdToUUID.put(user.getLoginId(), user.getId());

		return user;
	}

	@Override
	public User login(String loginId, String password) {
		if (loginId == null || password == null) return null;
		if (loginIdToUUID.containsKey(loginId)) {
			User user = UserMap.get(loginIdToUUID.get(loginId));
			if (user.getPassword().equals(password)) {
				return user;
			} else {
				return null;
			}
		}
		return null;
	}

	@Override
	public User getUserById(UUID id) {
		return UserMap.get(id);
	}

	@Override
	public User getUserByLoginId(String loginId) {
		return UserMap.get(loginIdToUUID.get(loginId));
	}

	@Override
	public List<User> getAllUsers() {
		List<User> userList = new ArrayList<>(UserMap.values());
		userList.sort((u1, u2) -> u1.getDefaultNickname().compareTo(u2.getDefaultNickname()));
		return userList;
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

	public boolean isExistLoginId(String loginId) {
		return loginIdToUUID.containsKey(loginId);
	}
}
