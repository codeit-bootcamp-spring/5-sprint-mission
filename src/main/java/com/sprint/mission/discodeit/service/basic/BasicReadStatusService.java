package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.CreateReadStatusDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateReadStatusDTO;
import com.sprint.mission.discodeit.domain.dto.readStatus.ReadStatusDto;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readStatus.ReadStatusDuplicateException;
import com.sprint.mission.discodeit.exception.readStatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
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
	private final ReadStatusMapper readStatusMapper;

	@Override
	@Transactional
	public ReadStatusDto create(CreateReadStatusDTO dto) {

		UUID channelId = dto.getChannelId();
		UUID userId = dto.getUserId();

		Channel channel = channelRepository.findById(channelId).orElseThrow(ChannelNotFoundException::new);

		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new
		);

		if (readStatusRepository.findByUserIdAndChannelId(userId, channelId).isPresent()) {
			throw new ReadStatusDuplicateException(Map.of("userId", userId, "channelId", channelId));
		}

		ReadStatus readStatuses = new ReadStatus(user, channel);
		readStatusRepository.save(readStatuses);
		return readStatusMapper.toDto(readStatuses);
	}

	@Override
	@Transactional
	public ReadStatusDto update(UpdateReadStatusDTO dto) {

		UUID id = dto.getId();
		Instant newLastReadAt = dto.getNewLastReadAt();

		ReadStatus readStatuses = readStatusRepository.findReadStatusDetailsById(id)
		  .orElseThrow(() -> new ReadStatusNotFoundException(Map.of("id", id)));

		readStatuses.setLastReadAt(newLastReadAt);
		readStatusRepository.save(readStatuses);
		return readStatusMapper.toDto(readStatuses);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReadStatusDto> findAllByUserId(UUID userId) {
		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException(Map.of("id", userId));
		}

		return readStatusRepository.findReadStatusDetailAllByUserId(userId)
		  .stream()
		  .map(readStatusMapper::toDto)
		  .toList();
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		readStatusRepository.deleteById(id);
	}
}
