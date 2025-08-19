package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.channel.DeleteChannelRequest;
import com.sprint.mission.discodeit.dto.request.channel.GetChannelByChannelIdRequest;
import com.sprint.mission.discodeit.dto.request.channel.GetChannelBychannelName;
import com.sprint.mission.discodeit.dto.request.channel.GetChannelsByUserRequest;
import com.sprint.mission.discodeit.dto.request.channel.LeaveChannelRequest;
import com.sprint.mission.discodeit.dto.request.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.CreateChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.DeleteChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.LeaveChannelResponse;
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
	public CreateChannelResponse create(CreatePublicChannelRequest request) {
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

		return CreateChannelResponse.success(channel);
	}

	@Override
	public CreateChannelResponse create(CreatePrivateChannelRequest request) {
		List<UUID> participantIds = request.getParticipantIds().stream().distinct().toList();

		for (UUID userId : participantIds) {
			if (userId == null) {
				throw new IllegalArgumentException("참여자 ID는 null일 수 없습니다.");
			}
			if (!userRepository.existsById(userId)) {
				throw new UserNotFoundException();
			}
		}

		Channel channel = new Channel(participantIds);
		channelRepository.save(channel);

		for (UUID userId : participantIds) {
			ReadStatus readStatus = new ReadStatus(userId, channel.getId());
			readStatusRepository.save(readStatus);
		}

		return CreateChannelResponse.successWithMembers(channel, participantIds);
	}

	@Override
	public ChannelResponse getChannelByName(GetChannelBychannelName request) {
		Channel channel = channelRepository.findByName(request.getChannelName())
			.orElseThrow(ChannelNotFoundException::new);

		return createChannelByType(channel);
	}

	@Override
	public ChannelResponse getChannelByUUID(GetChannelByChannelIdRequest request) {
		Channel channel = channelRepository.findById(request.getId())
			.orElseThrow(ChannelNotFoundException::new);

		return createChannelByType(channel);
	}

	@Override
	public List<ChannelResponse> getChannelsByUserId(GetChannelsByUserRequest request) {
		UUID userId = request.getUserId();

		List<UUID> myChannelIds = readStatusRepository.findByUserId(userId).stream()
				.map(ReadStatus::getChannelId)
				.toList();

		return channelRepository.findAll().stream()
				.filter(channel -> myChannelIds.contains(channel.getId()))
				.map(this::createChannelByType)
				.toList();
	}


	// updateChannel 메서드
	@Override
	public ChannelResponse updateChannel(UpdateChannelRequest request) {
		Channel channel = channelRepository.findById(request.getChannelId())
			.orElseThrow(ChannelNotFoundException::new);

		if (channel.getType().equals("PRIVATE")) {
			throw new PrivateChannelUpdateException();
		}

		if (channelRepository.existsByName(request.getNewName())) {
			throw new DuplicateChannelNameException();
		}

		channel.setName(request.getNewName());
		channel.setDescription(request.getNewDescription());
		channel.updateUpdatedAt();
		channelRepository.save(channel);

		return createChannelByType(channel);
	}

	@Override
	public LeaveChannelResponse leaveChannel(LeaveChannelRequest request) {
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

		return LeaveChannelResponse.success(channel, request.getUserId(), nickname);
	}

	@Override
	public DeleteChannelResponse deleteChannel(DeleteChannelRequest request) {
		Channel channel = channelRepository.findById(request.getChannelId())
			.orElseThrow(ChannelNotFoundException::new);
		messageRepository.deleteByChannelId(request.getChannelId());
		channelRepository.deleteById(request.getChannelId());
		return DeleteChannelResponse.success(channel);
	}

	private ChannelResponse createChannelByType(Channel channel) {
		Instant lastMessageTime = getLastMessageTime(channel.getId());

		if ("PRIVATE".equals(channel.getType())) {
			List<UUID> participantIds = getPrivateChannelParticipants(channel.getId());
			return ChannelResponse.fromPrivateChannel(channel, lastMessageTime, participantIds);
		} else {
			return ChannelResponse.fromPublicChannel(channel, lastMessageTime);
		}
	}

	private Instant getLastMessageTime(UUID channelId) {
		List<Message> messages = messageRepository.findByChannelId(channelId);
		return messages.stream()
			.map(Message::getCreatedAt)
			.max(Instant::compareTo)
			.orElse(null);
	}

	private List<UUID> getPrivateChannelParticipants(UUID channelId) {
		return readStatusRepository.findByChannelId(channelId)
			.stream()
			.map(ReadStatus::getUserId)
			.toList();
	}
}
