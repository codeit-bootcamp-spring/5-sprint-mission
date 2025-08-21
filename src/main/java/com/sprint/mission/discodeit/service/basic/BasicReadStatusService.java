package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.domain.dto.CreateReadStatusDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateReadStatusDTO;
import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.domain.response.CreateReadStatusResponse;
import com.sprint.mission.discodeit.domain.response.GetReadStatusResponse;
import com.sprint.mission.discodeit.domain.response.UpdateReadStatusResponse;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

	private final ReadStatusRepository readStatusRepository;
	private final UserRepository userRepository;
	private final ChannelRepository channelRepository;

	@Override
	public ReadStatus create(CreateReadStatusDTO dto) {

		UUID channelId = dto.getChannelId();
		UUID userId = dto.getUserId();
		Instant lastReadAt = dto.getLastReadAt();

		if (!channelRepository.existsById(channelId)) {
			throw new NoSuchElementException("channel with id " + channelId + "not found");
		}
		if (userRepository.isEmpty(userId)) {
			throw new NoSuchElementException("user with id " + userId + "not found");

		}
		if (readStatusRepository.findByUserIdAndChannelId(userId, channelId).isPresent()) {
			throw new IllegalArgumentException(
			  "ReadStatus with userId " + userId + "  and channelId " + channelId + " already exists");
		}

		ReadStatus readStatus = new ReadStatus(userId, channelId, lastReadAt);

		return readStatusRepository.save(readStatus);
	}

	@Override
	public ReadStatus update(UpdateReadStatusDTO dto) {

		UUID id = dto.getId();
		Instant newLastReadAt = dto.getNewLastReadAt();

		ReadStatus readStatus = readStatusRepository.find(id)
		  .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + id + " not found"));

		readStatus.setLastReadAt(newLastReadAt);

		return readStatusRepository.save(readStatus);
	}

	@Override
	public List<ReadStatus> findAllByUserId(UUID userId) {
		if (userRepository.isEmpty(userId)) {
			throw new NoSuchElementException("user with id " + userId + "not found");
		}

		return readStatusRepository.findAllByUserId(userId);
	}

	@Override
	public void delete(UUID id) {
		if (id == null || readStatusRepository.isEmpty(id)) {
			throw new IllegalArgumentException("Read status ID cannot be null or empty");
		}

		readStatusRepository.delete(id);
	}

	public static CreateReadStatusResponse toCreateReadStatusResponse(ReadStatus readStatus) {
		return CreateReadStatusResponse.builder()
		  .id(readStatus.getId())
		  .userId(readStatus.getUserId())
		  .channelId(readStatus.getChannelId())
		  .lastReadAt(readStatus.getLastReadAt())
		  .createdAt(readStatus.getCreatedAt())
		  .updatedAt(readStatus.getUpdatedAt())
		  .build();
	}

	public static UpdateReadStatusResponse toUpdateReadStatusResponse(ReadStatus readStatus) {
		return UpdateReadStatusResponse.builder()
		  .id(readStatus.getId())
		  .userId(readStatus.getUserId())
		  .channelId(readStatus.getChannelId())
		  .lastReadAt(readStatus.getLastReadAt())
		  .createdAt(readStatus.getCreatedAt())
		  .updatedAt(readStatus.getUpdatedAt())
		  .build();
	}

	public static List<GetReadStatusResponse> toGetReadStatusResponses(List<ReadStatus> readStatuses) {
		return readStatuses.stream()
		  .map(rs -> GetReadStatusResponse.builder()
			.id(rs.getId())
			.userId(rs.getUserId())
			.channelId(rs.getChannelId())
			.lastReadAt(rs.getLastReadAt())
			.createdAt(rs.getCreatedAt())
			.updatedAt(rs.getUpdatedAt())
			.build())
		  .toList();

	}

}
