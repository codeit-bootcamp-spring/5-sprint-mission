package com.sprint.mission.discodeit.service.basic;

import static com.sprint.mission.discodeit.domain.enums.ChannelType.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.CreatePrivateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.CreatePublicChannelDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
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
	private final BinaryContentMapper binaryContentMapper;

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
		List<User> participants = userRepository.findUsersWithProfileByIdIn(dto.getUserIds());
		if (participants.size() != dto.getUserIds().size()) {
			throw new IllegalArgumentException("missing participants ID Contain");
		}

		List<ReadStatus> newReadStatuses = participants.stream().map(u ->
		  new ReadStatus(
			u,
			newChannel
		  )
		).toList();
		readStatusRepository.saveAll(newReadStatuses);

		List<UserStatus> userStatuses = userStatusRepository.findByUserIdIn(
		  participants.stream().map(User::getId).toList());

		Map<UUID, Boolean> userID2IsOnlineMap = userStatuses.stream()
		  .collect(Collectors.toMap(
			us -> us.getUser().getId(), // key: User ID
			UserStatus::isOnline       // value: online 상태
		  ));

		List<UserDto> userDtoList = participants.stream()
		  .map(u -> userMapper.toDto(
			  u,
			  userID2IsOnlineMap.get(u.getId()),
			  binaryContentMapper.toDto(u.getProfileImage())
			)
		  ).toList();

		Instant lastMessageAt = newReadStatuses.stream()
		  .map(ReadStatus::getLastReadAt)
		  .max(Comparator.naturalOrder())
		  .orElse(null); // 없으면 null 리턴

		return channelMapper.toDto(newChannel, userDtoList, lastMessageAt);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ChannelDto> readAllByUserId(UUID userId) {
		// 필터링: PUBLIC 채널 또는 사용자가 참여한 PRIVATE 채널
		List<Channel> publicChannels = channelRepository.findPublicChannels(PUBLIC);
		List<ReadStatus> readStatusesOnlyUser = readStatusRepository.findAllByUserId(userId);
		List<Channel> privateChannels = readStatusesOnlyUser.stream()
		  .map(ReadStatus::getChannel).filter(channel -> channel.getType() == PRIVATE).toList();
		List<Channel> allChannels = Stream.concat(publicChannels.stream(), privateChannels.stream()).toList();

		List<ReadStatus> allReadStatusesDetails = readStatusRepository.findReadStatusDetailAllByChannelIds(
		  allChannels.stream().map(Channel::getId).toList());

		Map<UUID, List<User>> ChannelID2Participants = new HashMap<>();

		// 퍼블릭 채널은 participants 빈 배열
		publicChannels.forEach(c -> ChannelID2Participants.put(c.getId(), List.of()));

		// PRIVATE 채널 참여자 추가
		allReadStatusesDetails.stream()
		  .filter(rs -> rs.getChannel().getType() == PRIVATE)
		  .forEach(rs -> {
			  UUID channelID = rs.getChannel().getId();
			  // 기존 리스트 가져오기
			  List<User> participants = ChannelID2Participants.computeIfAbsent(channelID, k -> new ArrayList<>());
			  participants.add(rs.getUser());
		  });

		// 1. channel to lastMessagedAt Map
		Map<UUID, Instant> ChannelId2lastMessagedAtMap = new HashMap<>();
		allReadStatusesDetails.forEach(rs -> {
			UUID channelID = rs.getChannel().getId();
			ChannelId2lastMessagedAtMap.compute(channelID, (k, v) -> {
				if (v == null) {
					return rs.getLastReadAt();
				}
				return v.isAfter(rs.getLastReadAt()) ? rs.getLastReadAt() : v;
			});
		});

		List<UserStatus> userStatuses = userStatusRepository.findByUserIdIn(
		  allReadStatusesDetails.stream().map(rs -> rs.getUser().getId()).toList());

		Map<UUID, Boolean> UserId2IsOnlineMap = new HashMap<>();
		userStatuses.forEach(us -> UserId2IsOnlineMap.put(us.getUser().getId(), us.isOnline()));

		return allChannels.stream().map(c -> channelMapper.toDto(
			c,
			ChannelID2Participants.get(c.getId())
			  .stream()
			  .map(u -> userMapper.toDto(u, UserId2IsOnlineMap.get(u.getId()),
				binaryContentMapper.toDto(u.getProfileImage())))
			  .toList(),
			ChannelId2lastMessagedAtMap.get(c.getId())))
		  .toList();
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
		  .map(u -> userMapper.toDto(u, userID2IsOnlineMap.getOrDefault(u.getId(), false),
			binaryContentMapper.toDto(u.getProfileImage())))
		  .toList();

		Instant lastMessageAt = readStatuses.stream()
		  .map(ReadStatus::getLastReadAt)
		  .max(Comparator.naturalOrder())
		  .orElse(null);

		return channelMapper.toDto(channel, userDtos, lastMessageAt);
	}

}
