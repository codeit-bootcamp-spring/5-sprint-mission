package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service("readStatusService")
@Validated
public class BasicReadStatusService implements ReadStatusService {

	private final UserRepository userRepository;
	private final ChannelRepository channelRepository;
	private final ReadStatusRepository readStatusRepository;
	private final ReadStatusMapper readStatusMapper;

	@Override
	@Transactional
	public ReadStatusDto create(@Valid ReadStatusCreateRequest request) {
		User user = userRepository.findById(request.userId())
			.orElseThrow(() -> new UserNotFoundException().addDetail("user", request.userId()));

		Channel channel = channelRepository.findById(request.channelId())
			.orElseThrow(
				() -> new ChannelNotFoundException().addDetail("channel", request.channelId()));

		if (readStatusRepository.findAllByUserId(request.userId()).stream()
			.anyMatch(status -> status.getChannel().getId().equals(request.channelId()))) {
			throw new ReadStatusAlreadyExistsException();
		}

		ReadStatus readStatus = new ReadStatus(channel.getCreatedAt(), user, channel);
		return readStatusMapper.toDto(readStatusRepository.save(readStatus));
	}

	@Override
	@Transactional(readOnly = true)
	public ReadStatusDto findById(UUID id) {
		return readStatusMapper.toDto(validateReadStatus(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReadStatusDto> findAllByUserId(UUID userId) {
		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException().addDetail("userId", userId);
		}
		return readStatusRepository.findAllByUserId(userId).stream()
			.map(readStatusMapper::toDto)
			.toList();
	}

	@Override
	@Transactional
	public ReadStatusDto update(UUID readStatusId,
		@Valid ReadStatusUpdateRequest readStatusUpdateRequest) {
		ReadStatus readStatus = validateReadStatus(readStatusId);
		readStatus.update(readStatusUpdateRequest.newLastReadAt());

		return readStatusMapper.toDto(readStatusRepository.save(readStatus));
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		validateReadStatus(id);
		readStatusRepository.deleteById(id);
	}

	public ReadStatus validateReadStatus(UUID readStatusId) {
		return readStatusRepository.findById(readStatusId)
			.orElseThrow(() -> new ReadStatusNotFoundException().addDetail("id", readStatusId));
	}
}
