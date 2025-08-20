package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.channel.ChannelLeaveRequest;
import com.sprint.mission.discodeit.dto.request.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelCreateResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelDeleteResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelLeaveResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.DuplicateChannelNameException;
import com.sprint.mission.discodeit.exception.channel.NotChannelMemberException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
	private final ChannelRepository channelRepository;
	private final ReadStatusRepository readStatusRepository;
	private final MessageRepository messageRepository;
	private final UserRepository  userRepository;


	@Override
	public ChannelCreateResponse create(PublicChannelCreateRequest request) {
		if (channelRepository.existsByName(request.getName())) {
			throw new DuplicateChannelNameException();
		}

		Channel channel = new Channel(request.getName(), request.getDescription());
		channelRepository.save(channel);

		List<User> allUsers = userRepository.findAll();
		for (User user : allUsers) {
			ReadStatus readStatus = new ReadStatus(user.getId(), channel.getId());
			readStatusRepository.save(readStatus);
		}

		return ChannelCreateResponse.success(channel);
	}

	@Override
	public ChannelCreateResponse create(PrivateChannelCreateRequest request) {
		List<java.util.UUID> participantIds = request.getParticipantIds().stream().distinct().toList();

		for (java.util.UUID userId : participantIds) {
			if (userId == null) {
				throw new IllegalArgumentException("참여자 ID는 null일 수 없습니다.");
			}
			if (!userRepository.existsById(userId)) {
				throw new UserNotFoundException();
			}
		}

		Channel channel = new Channel(participantIds);
		channelRepository.save(channel);

		for (java.util.UUID userId : participantIds) {
			ReadStatus readStatus = new ReadStatus(userId, channel.getId());
			readStatusRepository.save(readStatus);
		}

		return ChannelCreateResponse.successWithMembers(channel, participantIds);
	}

	@Override
	public ChannelResponse findByName(String channelName) {
		Channel channel = channelRepository.findByName(channelName)
			.orElseThrow(ChannelNotFoundException::new);

		return createChannelByType(channel);
	}

	@Override
	public ChannelResponse find(UUID channelId) {
		Channel channel = channelRepository.findById(channelId)
			.orElseThrow(ChannelNotFoundException::new);

		return createChannelByType(channel);
	}

	@Override
	public List<ChannelResponse> findChannelsByUserId(UUID userId) {

		List<java.util.UUID> myChannelIds = readStatusRepository.findByUserId(userId).stream()
				.map(ReadStatus::getChannelId)
				.toList();

		return channelRepository.findAll().stream()
				.filter(channel -> myChannelIds.contains(channel.getId()))
				.map(this::createChannelByType)
				.toList();
	}


	// updateChannel 메서드
	@Override
	public ChannelResponse updateChannel(UUID channelId, ChannelUpdateRequest request) {
		Channel channel = channelRepository.findById(channelId)
			.orElseThrow(ChannelNotFoundException::new);

		if (channel.getType().equals("PRIVATE")) {
			throw new PrivateChannelUpdateException();
		}

		if (request.getNewName() != null &&channelRepository.existsByName(request.getNewName())) {
			throw new DuplicateChannelNameException();
		}

		if (request.getNewName() != null) {
			channel.setName(request.getNewName());
			channel.updateUpdatedAt();
		}
		if (request.getNewDescription() != null) {
			channel.setDescription(request.getNewDescription());
			channel.updateUpdatedAt();
		}
		channelRepository.save(channel);

		return createChannelByType(channel);
	}

	@Override
	public ChannelLeaveResponse leaveChannel(ChannelLeaveRequest request) {
		Channel channel = channelRepository.findById(request.getChannelId())
				.orElseThrow(ChannelNotFoundException::new);

		List<ReadStatus> readStatuses = readStatusRepository.findByChannelIdAndUserId(
				request.getChannelId(), request.getUserId());

		if (readStatuses.isEmpty()) {
			throw new NotChannelMemberException();
		}

		for (ReadStatus readStatus : readStatuses) {
			readStatusRepository.deleteById(readStatus.getId());
		}

		channel.updateUpdatedAt();
		channelRepository.save(channel);

		String nickname = userRepository.findById(request.getUserId())
				.map(User::getDefaultNickname)
				.orElseThrow(UserNotFoundException::new);

		return ChannelLeaveResponse.success(channel, request.getUserId(), nickname);
	}

	@Override
	public ChannelDeleteResponse deleteChannel(UUID channelId) {
		// userId는 나중에 admin 혹은 권한 체크를 위해서 남겨둠

		Channel channel = channelRepository.findById(channelId)
			.orElseThrow(ChannelNotFoundException::new);
		messageRepository.deleteByChannelId(channelId);
		channelRepository.deleteById(channelId);
		return ChannelDeleteResponse.success(channel);
	}

	private ChannelResponse createChannelByType(Channel channel) {
		Instant lastMessageTime = getLastMessageTime(channel.getId());

		if ("PRIVATE".equals(channel.getType())) {
			List<java.util.UUID> participantIds = getPrivateChannelParticipants(channel.getId());
			return ChannelResponse.fromPrivateChannel(channel, lastMessageTime, participantIds);
		} else {
			return ChannelResponse.fromPublicChannel(channel, lastMessageTime);
		}
	}

	private Instant getLastMessageTime(java.util.UUID channelId) {
		List<Message> messages = messageRepository.findByChannelId(channelId);
		return messages.stream()
			.map(Message::getCreatedAt)
			.max(Instant::compareTo)
			.orElse(null);
	}

	private List<java.util.UUID> getPrivateChannelParticipants(java.util.UUID channelId) {
		return readStatusRepository.findByChannelId(channelId)
			.stream()
			.map(ReadStatus::getUserId)
			.toList();
	}
}
