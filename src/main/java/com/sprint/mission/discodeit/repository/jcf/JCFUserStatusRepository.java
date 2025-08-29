package com.sprint.mission.discodeit.repository.jcf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

@Repository
@ConditionalOnProperty(
  prefix = "discodeit.repository",
  name = "type",
  havingValue = "jcf",
  matchIfMissing = true // 값이 없으면 JCF로 등록
)
public class JCFUserStatusRepository implements UserStatusRepository {

	private final Map<UUID, UserStatus> data;

	public JCFUserStatusRepository() {
		this.data = new HashMap<>();
	}

	@Override
	public UserStatus save(UserStatus userStatus) {
		data.put(userStatus.getId(), userStatus);
		return userStatus;
	}

	@Override
	public Optional<UserStatus> find(UUID id) {
		return Optional.ofNullable(data.get(id));
	}

	@Override
	public Optional<UserStatus> findByUserId(UUID userId) {
		return data.values().stream()
		  .filter(userStatus -> userStatus.getUser().getId().equals(userId))
		  .findFirst();
	}

	@Override
	public List<UserStatus> findAll() {
		return data.values().stream().toList();
	}

	@Override
	public void delete(UUID id) {
		if (!data.containsKey(id)) {
			throw new IllegalArgumentException("UserStatus with ID " + id + " not found");
		}
		data.remove(id);
	}

	@Override
	public boolean isEmpty(UUID id) {
		return !data.containsKey(id) || data.get(id) == null;
	}

	@Override
	public void deleteAll() {
		data.clear();
	}

	@Override
	public void deleteByUserId(UUID userId) {
		data.values().removeIf(userStatus -> userStatus.getUser().getId().equals(userId));
	}
}
