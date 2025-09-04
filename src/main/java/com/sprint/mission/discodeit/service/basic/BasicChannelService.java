package com.sprint.mission.discodeit.service.basic;

import static com.sprint.mission.discodeit.domain.enums.ChannelType.*;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.CreatePrivateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.CreatePublicChannelDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

	private final ChannelRepository channelRepository;
	private final MessageRepository messageRepository;
	private final ReadStatusRepository readStatusRepository;
	private final UserRepository userRepository;
	private final ChannelMapper channelMapper;
	private final UserStatusRepository userStatusRepository;
	private final UserMapper userMapper;

	@Override
	@Transactional
	public ChannelDto createPublic(CreatePublicChannelDTO dto) {
		String name = dto.getName();
		String description = dto.getDescription();

		Channel savedChannel = channelRepository.save(new Channel(PUBLIC, name, description));

		return buildChannelDto(savedChannel);
	}

	@Override
	@Transactional
	public ChannelDto createPrivate(CreatePrivateChannelDTO dto) {

		Channel newChannel = new Channel(PRIVATE);
		channelRepository.save(newChannel);
		List<ReadStatus> newReadStatuses = dto.getUserIds().stream().map(id ->
		  new ReadStatus(
			userRepository.findById(id)
			  .orElseThrow(() -> new NoSuchElementException("userId with" + id + "notFound")),
			newChannel
		  )
		).toList();
		readStatusRepository.saveAll(newReadStatuses);

		List<User> participants = newReadStatuses.stream().map(ReadStatus::getUser).toList();
		List<UserStatus> userStatuses = userStatusRepository.findByUserIdIn(
		  participants.stream().map(User::getId).toList());

		Map<UUID, Boolean> userID2IsOnlineMap = userStatuses.stream()
		  .collect(Collectors.toMap(
			us -> us.getUser().getId(), // key: User ID
			UserStatus::isOnline       // value: online 상태
		  ));

		List<UserDto> userDtoList = participants.stream()
		  .map(u -> userMapper.toDto(u, userID2IsOnlineMap.get(u.getId())))
		  .toList();

		Instant lastMessageAt = newReadStatuses.stream()
		  .map(ReadStatus::getLastReadAt)
		  .max(Comparator.naturalOrder())
		  .orElse(null); // 없으면 null 리턴

		return channelMapper.toDto(newChannel, userDtoList, lastMessageAt);
	}

	@Override
	@Transactional(readOnly = true)
	// TODO N+1문제 해결해야함
	public List<ChannelDto> readAllByUserId(UUID userId) {
		// 필터링: PUBLIC 채널 또는 사용자가 참여한 PRIVATE 채널
		List<Channel> filteredChannels = channelRepository.findAll().stream()
		  .filter(c -> c.getType() == PUBLIC || readStatusRepository.findAllByUserId(userId)
			.stream()
			.anyMatch(us -> us.getChannel().getId().equals(c.getId())))
		  .toList();

		return filteredChannels.stream().map(this::buildChannelDto).toList();
	}

	@Override
	@Transactional
	public boolean delete(UUID id) {
		if (!channelRepository.existsById(id)) {
			throw new NoSuchElementException("Channel with id " + id + " not found");
		}
		// 연관된 메시지도 삭제
		messageRepository.deleteByChannelId(id);
		// 연관된 유저 상태도 삭제
		readStatusRepository.deleteByChannelId(id);

		channelRepository.deleteById(id);

		return true;
	}

	@Override
	@Transactional
	public ChannelDto update(UpdateChannelDTO dto) {
		UUID id = dto.getId();
		String newChannelName = dto.getName();
		String newDescription = dto.getDescription();

		// 채널이 존재하는지 확인
		Channel targetChannel = channelRepository.findById(id)
		  .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));

		if (targetChannel.getType() == PRIVATE) {
			throw new IllegalArgumentException(
			  "Private channel cannot be updated");
		}

		if (newChannelName != null) {
			targetChannel.setName(newChannelName);

		}
		if (newDescription != null) {
			targetChannel.setDescription(newDescription);
		}
		channelRepository.save(targetChannel);

		return buildChannelDto(targetChannel);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isEmpty(UUID id) {
		return !channelRepository.existsById(id);
	}

	private Instant getMessageLastEditAt(Message message) {
		return message.getUpdatedAt() != null ? message.getUpdatedAt() : message.getCreatedAt();
	}

	private ChannelDto buildChannelDto(Channel channel) {
		List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channel.getId());

		List<User> participants = readStatuses.stream()
		  .map(ReadStatus::getUser)
		  .toList();

		List<UserStatus> userStatuses = userStatusRepository.findByUserIdIn(
		  participants.stream().map(User::getId).toList()
		);

		Map<UUID, Boolean> userID2IsOnlineMap = userStatuses.stream()
		  .collect(Collectors.toMap(
			us -> us.getUser().getId(),
			UserStatus::isOnline
		  ));

		List<UserDto> userDtos = participants.stream()
		  .map(u -> userMapper.toDto(u, userID2IsOnlineMap.getOrDefault(u.getId(), false)))
		  .toList();

		Instant lastMessageAt = readStatuses.stream()
		  .map(ReadStatus::getLastReadAt)
		  .max(Comparator.naturalOrder())
		  .orElse(null);

		return channelMapper.toDto(channel, userDtos, lastMessageAt);
	}

}
