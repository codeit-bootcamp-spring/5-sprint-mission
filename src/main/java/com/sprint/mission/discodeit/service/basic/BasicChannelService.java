package com.sprint.mission.discodeit.service.basic;

import static com.sprint.mission.discodeit.domain.enums.ChannelType.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionRegistry;
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
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ParticipantsEmptyException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateNotAllowedException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.SecurityService;
import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicChannelService implements ChannelService {

	private final ChannelRepository channelRepository;
	private final MessageRepository messageRepository;
	private final ReadStatusRepository readStatusRepository;
	private final UserRepository userRepository;
	private final ChannelMapper channelMapper;
	private final UserMapper userMapper;
	private final BinaryContentMapper binaryContentMapper;
	private final SessionRegistry sessionRegistry;
	private final SecurityService securityService;

	@Override
	@Transactional
	@PreAuthorize("hasRole('CHANNEL_MANAGER')")
	public ChannelDto createPublic(CreatePublicChannelDTO dto) {
		log.debug("PUBLIC channel create 트랜잭션 시작");
		String name = dto.getName();
		String description = dto.getDescription();

		Channel savedChannel = channelRepository.save(new Channel(PUBLIC, name, description));
		log.debug("success save channelEntity  channelID={}", savedChannel.getId());

		log.debug("PUBLIC channel create 트랜잭션 정상 종료");
		return buildChannelDto(savedChannel);
	}

	@Override
	@Transactional
	public ChannelDto createPrivate(CreatePrivateChannelDTO dto) {
		log.debug("PRIVATE channel create 트랜잭션 시작");

		Channel newChannel = new Channel(PRIVATE);
		channelRepository.save(newChannel);
		log.debug("success save channelEntity  channelUserIDs={}", dto.getUserIds().toString());

		List<User> participants = userRepository.findUsersWithProfileByIdIn(dto.getUserIds());
		if (participants.isEmpty() || participants.size() != dto.getUserIds().size()) {
			log.error("participant Ids blank");
			throw new ParticipantsEmptyException();
		}

		List<ReadStatus> newReadStatuses = participants.stream().map(u ->
		  new ReadStatus(
			u,
			newChannel
		  )
		).toList();
		readStatusRepository.saveAll(newReadStatuses);
		log.debug("success save readStatuses  channelUserIDs={}", dto.getUserIds().toString());

		Set<UUID> participantIds = participants.stream()
		  .map(User::getId)
		  .collect(Collectors.toSet());

		Map<UUID, Boolean> userID2IsOnlineMap = securityService.getUserId2OnlineMap(participantIds);

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

		log.debug("PRIVATE channel create 트랜잭션 정상 종료");
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

		Set<UUID> userIds = ChannelID2Participants.values().stream()
		  .flatMap(users -> users.stream().map(User::getId))
		  .collect(Collectors.toSet());
		Map<UUID, Boolean> UserId2IsOnlineMap = securityService.getUserId2OnlineMap(userIds);

		return allChannels.stream().map(c -> channelMapper.toDto(
			c,
			ChannelID2Participants.get(c.getId())
			  .stream()
			  .map(u -> userMapper.toDto(u, Optional.ofNullable(UserId2IsOnlineMap.get(u.getId())).orElse(false),
				binaryContentMapper.toDto(u.getProfileImage())))
			  .toList(),
			ChannelId2lastMessagedAtMap.get(c.getId())))
		  .toList();

	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('CHANNEL_MANAGER')")
	public boolean delete(UUID id) {
		log.debug("channel delete 트랜잭션 시작");

		if (!channelRepository.existsById(id)) {
			log.error("channel id does not found");
			throw new ChannelNotFoundException();
		}
		// 연관된 메시지도 삭제
		messageRepository.deleteByChannelId(id);
		log.debug("success delete messages By channelID={}", id);

		// 연관된 유저 상태도 삭제
		readStatusRepository.deleteByChannelId(id);
		log.debug("success delete readStatuses By channelID={}", id);

		channelRepository.deleteById(id);
		log.debug("success delete channelEntity channelID={}", id);

		log.debug("channel delete 트랜잭션 정상 종료");
		return true;
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('CHANNEL_MANAGER')")
	public ChannelDto update(UpdateChannelDTO dto) {
		log.debug("channel update 트랜잭션 시작");

		UUID id = dto.getId();
		String newChannelName = dto.getName();
		String newDescription = dto.getDescription();
		log.debug("new channel info id={} newChanelName={}, newChannelDescription={}"
		  , id, newChannelName, newDescription);
		// 채널이 존재하는지 확인
		Channel targetChannel = channelRepository.findById(id)
		  .orElseThrow(ChannelNotFoundException::new);

		if (targetChannel.getType() == PRIVATE) {
			log.error("bad request : Private channel cannot be updated");
			throw new PrivateChannelUpdateNotAllowedException();
		}

		if (newChannelName != null) {
			targetChannel.setName(newChannelName);

		}
		if (newDescription != null) {
			targetChannel.setDescription(newDescription);
		}
		channelRepository.save(targetChannel);
		log.debug("success update channelEntity  channelId={}", id);

		log.debug("channel update 트랜잭션 정상 종료");
		return buildChannelDto(targetChannel);
	}

	private ChannelDto buildChannelDto(Channel channel) {
		List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channel.getId());

		List<User> participants = readStatuses.stream()
		  .map(ReadStatus::getUser)
		  .toList();

		Set<UUID> userIds = participants.stream().map(User::getId).collect(Collectors.toSet());
		Map<UUID, Boolean> userID2IsOnlineMap = securityService.getUserId2OnlineMap(userIds);

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
