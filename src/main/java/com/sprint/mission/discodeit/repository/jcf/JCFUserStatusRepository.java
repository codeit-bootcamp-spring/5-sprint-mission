package com.sprint.mission.discodeit.repository.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFUserStatusRepository implements UserStatusRepository {
	private final Map<UUID, UserStatus> userStatusMap;
	private final Map<UUID, UUID> userIdToStatusIdMap;

	public JCFUserStatusRepository() {
		userStatusMap = new ConcurrentHashMap<>();
		userIdToStatusIdMap = new ConcurrentHashMap<>();
	}

	@Override
	public void save(UserStatus status) {
		if (status == null || status.getId() == null) {
			return;
		}
		userStatusMap.put(status.getId(), status);
		userIdToStatusIdMap.put(status.getUserId(), status.getId());
	}

	@Override
	public Optional<UserStatus> findById(UUID id) {
		return Optional.ofNullable(userStatusMap.get(id)).map(UserStatus::copy);
	}

	@Override
	public Optional<UserStatus> findByUserId(UUID userId) {
		UUID statusId = userIdToStatusIdMap.get(userId);
		if (statusId == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(userStatusMap.get(statusId)).map(UserStatus::copy);
	}

	@Override
	public List<UserStatus> findByUserIdIn(List<UUID> userIdList) {
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
			userIdToStatusIdMap.remove(userStatus.getUserId());
			userStatusMap.remove(id);
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
}