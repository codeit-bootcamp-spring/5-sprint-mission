package com.sprint.mission.discodeit.repository.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

public class JCFReadStatusRepository implements ReadStatusRepository {
	private final Map<UUID, ReadStatus> readStatusMap;
	private final Map<UUID, List<UUID>> userToReadStatusMap;    // userId -> List<readStatusId>
	private final Map<UUID, List<UUID>> channelToReadStatusMap; // channelId -> List<readStatusId>

	public JCFReadStatusRepository() {
		readStatusMap = new ConcurrentHashMap<>();
		userToReadStatusMap = new ConcurrentHashMap<>();
		channelToReadStatusMap = new ConcurrentHashMap<>();
	}

	@Override
	public void save(ReadStatus status) {
		if (status == null || status.getId() == null) {
			return;
		}

		readStatusMap.put(status.getId(), status);

		List<UUID> userReadStatusIds = userToReadStatusMap.computeIfAbsent(status.getUserId(), k -> new ArrayList<>());
		if (!userReadStatusIds.contains(status.getId())) {
			userReadStatusIds.add(status.getId());
		}

		List<UUID> channelReadStatusIds = channelToReadStatusMap.computeIfAbsent(status.getChannelId(), k -> new ArrayList<>());
		if (!channelReadStatusIds.contains(status.getId())) {
			channelReadStatusIds.add(status.getId());
		}
	}

	@Override
	public Optional<ReadStatus> findById(UUID id) {
		return Optional.ofNullable(readStatusMap.get(id)).map(ReadStatus::copy);
	}

	@Override
	public List<ReadStatus> findByUserId(UUID userId) {
		if (userId == null) {
			return List.of();
		}

		List<UUID> readStatusIds = userToReadStatusMap.get(userId);
		if (readStatusIds == null) {
			return List.of();
		}

		return readStatusIds.stream()
			.map(readStatusMap::get)
			.filter(Objects::nonNull)
			.map(ReadStatus::copy)
			.toList();
	}

	@Override
	public List<ReadStatus> findByChannelId(UUID channelId) {
		if (channelId == null) {
			return List.of();
		}

		List<UUID> readStatusIds = channelToReadStatusMap.get(channelId);
		if (readStatusIds == null) {
			return List.of();
		}

		return readStatusIds.stream()
			.map(readStatusMap::get)
			.filter(Objects::nonNull)
			.map(ReadStatus::copy)
			.toList();
	}

	@Override
	public List<ReadStatus> findByChannelIdAndUserId(UUID channelId, UUID userId) {
		if (channelId == null || userId == null) {
			return List.of();
		}

		List<UUID> channelIds = channelToReadStatusMap.getOrDefault(channelId, List.of());
		List<UUID> userIds = userToReadStatusMap.getOrDefault(userId, List.of());

		return channelIds.stream()
			.filter(userIds::contains)
			.map(readStatusMap::get)
			.filter(Objects::nonNull)
			.map(ReadStatus::copy)
			.toList();
	}

	@Override
	public List<ReadStatus> findAll() {
		return readStatusMap.values().stream()
			.map(ReadStatus::copy)
			.toList();
	}

	@Override
	public void deleteById(UUID id) {
		if (id == null) {
			return;
		}

		ReadStatus readStatus = readStatusMap.get(id);
		if (readStatus != null) {
			removeFromMaps(id, readStatus);
		}
	}

	@Override
	public void deleteByChannelId(UUID channelId) {
		if (channelId == null) {
			return;
		}

		List<UUID> readStatusIds = channelToReadStatusMap.get(channelId);
		if (readStatusIds != null) {
			List<UUID> idsToDelete = new ArrayList<>(readStatusIds);
			for (UUID id : idsToDelete) {
				ReadStatus readStatus = readStatusMap.get(id);
				if (readStatus != null) {
					removeFromMaps(id, readStatus);
				}
			}
		}
	}

	@Override
	public void deleteByUserIdAndChannelId(UUID userId, UUID channelId) {
		if (userId == null || channelId == null) {
			return;
		}
		List<ReadStatus> toDelete = findByChannelIdAndUserId(channelId, userId);
		for (ReadStatus rs : toDelete) {
			deleteById(rs.getId());
		}
	}

	@Override
	public boolean existsByChannelIdAndUserId(UUID channelId, UUID userId) {
		if (channelId == null || userId == null) {
			return false;
		}

		List<UUID> channelReadStatusIds = channelToReadStatusMap.get(channelId);
		if (channelReadStatusIds == null) {
			return false;
		}

		for (UUID readStatusId : channelReadStatusIds) {
			ReadStatus rs = readStatusMap.get(readStatusId);
			if (rs != null && rs.getUserId().equals(userId)) {
				return true;
			}
		}
		return false;
	}

	private void removeFromMaps(UUID id, ReadStatus readStatus) {
		readStatusMap.remove(id);
		removeFromUserIndex(id, readStatus.getUserId());
		removeFromChannelIndex(id, readStatus.getChannelId());
	}

	private void removeFromUserIndex(UUID id, UUID userId) {
		List<UUID> userReadStatusIds = userToReadStatusMap.get(userId);
		if (userReadStatusIds != null) {
			userReadStatusIds.remove(id);
			if (userReadStatusIds.isEmpty()) {
				userToReadStatusMap.remove(userId);
			}
		}
	}

	private void removeFromChannelIndex(UUID id, UUID channelId) {
		List<UUID> channelReadStatusIds = channelToReadStatusMap.get(channelId);
		if (channelReadStatusIds != null) {
			channelReadStatusIds.remove(id);
			if (channelReadStatusIds.isEmpty()) {
				channelToReadStatusMap.remove(channelId);
			}
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