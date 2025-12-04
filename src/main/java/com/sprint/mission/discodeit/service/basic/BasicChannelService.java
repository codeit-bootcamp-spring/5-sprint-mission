package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service("channelService")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BasicChannelService implements ChannelService {

	private final ChannelRepository channelRepository;
	private final ReadStatusRepository readStatusRepository;
	private final MessageRepository messageRepository;
	private final UserRepository userRepository;
	private final ChannelMapper channelMapper;

	@Override
	@Transactional
	@PreAuthorize("hasRole('CHANNEL_MANAGER')")
	@CacheEvict(value = "channels", allEntries = true)
	public ChannelDto create(PublicChannelCreateRequest request) {
		log.debug("[ChannelService#create(public)] try request={}", request);

		Channel channel = channelRepository.save(new Channel(
			ChannelType.PUBLIC,
			request.name(),
			request.description()));

		ChannelDto dto = channelMapper.toDto(
			channel,
			findParticipants(channel.getId()),
			findLastMessageAt(channel.getId()));

		log.info("[ChannelService#create(public)] Channel created: {}", dto.forLog());

		return dto;
	}

	@Override
	@Transactional
	@CacheEvict(value = "channels", allEntries = true)
	public ChannelDto create(PrivateChannelCreateRequest request) {
		log.debug("[ChannelService#create(private)] try request={}", request);

		List<User> participants = request.participantIds()
			.stream()
			.map(userId -> userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException().addDetail("userId", userId)))
			.toList();

		Channel channel = new Channel(ChannelType.PRIVATE, null, null);
		channelRepository.save(channel);

		for (User user : participants) {
			ReadStatus readStatus = readStatusRepository.save(
				new ReadStatus(channel.getCreatedAt(), user, channel, true));
			log.debug("[ChannelService#create(private)] ReadStatus created: readStatusId={}",
				readStatus.getId());
		}

		ChannelDto dto = channelMapper.toDto(
			channel,
			findParticipants(channel.getId()),
			findLastMessageAt(channel.getId()));
		log.info("[ChannelService#create(private)] Channel created: {}", dto.forLog());

		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	public ChannelDto findById(UUID channelId) {
		Channel channel = validateId(channelId);

		return channelMapper.toDto(channel, findParticipants(channelId), findLastMessageAt(channelId));
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "channels", key = "#userId")
	public List<ChannelDto> findAllByUserId(UUID userId) {
		List<UUID> ids = readStatusRepository.findAllByUserId(userId).stream()
			.map(readStatus -> readStatus.getChannel().getId())
			.toList();

		return channelRepository.findAllByIdInOrType(ids, ChannelType.PUBLIC)
			.stream()
			.map(channel ->
				channelMapper.toDto(
					channel,
					findParticipants(channel.getId()),
					findLastMessageAt(channel.getId())))
			.toList();
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('CHANNEL_MANAGER')")
	@CacheEvict(value = "channels", allEntries = true)
	public ChannelDto update(UUID channelId,
		PublicChannelUpdateRequest request) {
		log.debug("[ChannelService#update] try: channelId={}, request={}", channelId, request);
		Channel channel = validateId(channelId);

		if (channel.getType() == ChannelType.PRIVATE) {
			throw new PrivateChannelUpdateException().addDetail("channelId", channelId);
		}
		channel.update(request.newName(), request.newDescription());
		channelRepository.save(channel);

		ChannelDto dto = channelMapper.toDto(
			channel,
			findParticipants(channelId),
			findLastMessageAt(channelId));

		log.info("[ChannelService#update] Channel updated: {}", dto.forLog());

		return dto;
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('CHANNEL_MANAGER')")
	@CacheEvict(value = "channels", allEntries = true)
	public void delete(UUID channelId) {
		log.debug("[ChannelService#delete] try channelId={}", channelId);
		validateId(channelId);

		readStatusRepository.findAllByChannelId(channelId)
			.forEach(readStatus -> {
				readStatusRepository.deleteById(readStatus.getId());
				log.debug("[ChannelService#delete] ReadStatus deleted: readStatusId={}",
					readStatus.getId());
			});

		messageRepository.findAll().stream()
			.filter(message -> message.getChannel().getId().equals(channelId))
			.forEach(message -> {
				messageRepository.deleteById(message.getId());
				log.debug("[ChannelService#delete] Message deleted: messageId={}", message.getId());
			});

		channelRepository.deleteById(channelId);
		log.info("[ChannelService#delete] Channel deleted: channelId={}", channelId);
	}

	private List<User> findParticipants(UUID channelId) {
		return userRepository.findAllByIdIn(
			readStatusRepository.findAllByChannelId(channelId).stream()
				.map(readStatus -> readStatus.getUser().getId())
				.toList()
		);
	}

	private Instant findLastMessageAt(UUID channelId) {
		return messageRepository.findTopByChannelIdOrderByCreatedAtDescIdDesc(channelId)
			.map(Message::getCreatedAt)
			.orElse(null);
	}

	private Channel validateId(UUID channelId) {
		return channelRepository.findById(channelId)
			.orElseThrow(() -> new ChannelNotFoundException().addDetail("channelId", channelId));
	}
}
