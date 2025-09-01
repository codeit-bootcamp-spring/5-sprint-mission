package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.CreateReadStatusDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateReadStatusDTO;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.domain.entity.User;
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
	@Transactional
	public ReadStatus create(CreateReadStatusDTO dto) {

		UUID channelId = dto.getChannelId();
		UUID userId = dto.getUserId();
		Instant lastReadAt = dto.getLastReadAt();

		Channel channel = channelRepository.findById(channelId).orElseThrow(() ->
		  new NoSuchElementException("channel with id " + channelId + "not found"));

		User user = userRepository.findById(userId).orElseThrow(() ->
		  new NoSuchElementException("user with id " + userId + "not found")
		);

		if (readStatusRepository.findByUserIdAndChannelId(userId, channelId).isPresent()) {
			throw new IllegalArgumentException(
			  "ReadStatus with userId " + userId + "  and channelId " + channelId + " already exists");
		}

		ReadStatus readStatuses = new ReadStatus(user, channel);

		return readStatusRepository.save(readStatuses);
	}

	@Override
	@Transactional
	public ReadStatus update(UpdateReadStatusDTO dto) {

		UUID id = dto.getId();
		Instant newLastReadAt = dto.getNewLastReadAt();

		ReadStatus readStatuses = readStatusRepository.findById(id)
		  .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + id + " not found"));

		readStatuses.setLastReadAt(newLastReadAt);

		return readStatusRepository.save(readStatuses);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReadStatus> findAllByUserId(UUID userId) {
		if (!userRepository.existsById(userId)) {
			throw new NoSuchElementException("user with id " + userId + "not found");
		}

		return readStatusRepository.findAllByUserId(userId);
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		if (id == null || !readStatusRepository.existsById(id)) {
			throw new IllegalArgumentException("Read status ID cannot be null or empty");
		}

		readStatusRepository.deleteById(id);
	}

	public static CreateReadStatusResponse toCreateReadStatusResponse(ReadStatus readStatuses) {
		return CreateReadStatusResponse.builder()
		  .id(readStatuses.getId())
		  .userId(readStatuses.getUser().getId())
		  .channelId(readStatuses.getChannel().getId())
		  .lastReadAt(readStatuses.getLastReadAt())
		  .build();
	}

	public static UpdateReadStatusResponse toUpdateReadStatusResponse(ReadStatus readStatuses) {
		return UpdateReadStatusResponse.builder()
		  .id(readStatuses.getId())
		  .userId(readStatuses.getUser().getId())
		  .channelId(readStatuses.getChannel().getId())
		  .lastReadAt(readStatuses.getLastReadAt())
		  .build();
	}

	public static List<GetReadStatusResponse> toGetReadStatusResponses(List<ReadStatus> readStatuses) {
		return readStatuses.stream()
		  .map(rs -> GetReadStatusResponse.builder()
			.id(rs.getId())
			.userId(rs.getUser().getId())
			.channelId(rs.getChannel().getId())
			.lastReadAt(rs.getLastReadAt())
			.build())
		  .toList();

	}

}
