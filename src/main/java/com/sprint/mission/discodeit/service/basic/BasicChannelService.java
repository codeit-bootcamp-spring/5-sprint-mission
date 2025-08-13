package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.request.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.channel.DeleteChannelRequest;
import com.sprint.mission.discodeit.dto.request.channel.GetChannelByChannelIdRequest;
import com.sprint.mission.discodeit.dto.request.channel.GetChannelBychannelName;
import com.sprint.mission.discodeit.dto.request.channel.GetChannelsByUserRequest;
import com.sprint.mission.discodeit.dto.request.channel.JoinChannelRequest;
import com.sprint.mission.discodeit.dto.request.channel.LeaveChannelRequest;
import com.sprint.mission.discodeit.dto.request.channel.UpdateChannelnameRequest;
import com.sprint.mission.discodeit.dto.request.channel.UpdateUserNicknameRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.CreateChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.DeleteChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.JoinChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.LeaveChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.channel.AlreadyExistsChannelMemberException;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
	private final ChannelRepository channelRepository;
	private final ReadStatusRepository readStatusRepository;
	private final MessageRepository messageRepository;
	private final UserRepository  userRepository;

	@Override
	public CreateChannelResponse createPublicChannel(CreatePublicChannelRequest request) {
		if (channelRepository.existsByName(request.getChannelName())) {
			throw new DuplicateChannelNameException();
		}

		Channel channel = new Channel(request.getChannelName());
		channelRepository.save(channel);

		return CreateChannelResponse.success(channel);
	}

	@Override
	public CreateChannelResponse createPrivateChannel(CreatePrivateChannelRequest request) {
		List<UUID> uniqueMemberIds = request.getMemberIds().stream().distinct().toList();

		for (UUID userId : request.getMemberIds()) {
			if (userId == null) {
				throw new IllegalArgumentException("null");
			}

			if (!userRepository.existsById(userId)) {
				throw new UserNotFoundException();
			}
		}

		Channel channel = new Channel(uniqueMemberIds);

		channelRepository.save(channel);

		for (UUID userId : uniqueMemberIds) {
			ReadStatus readStatus = new ReadStatus(userId, channel.getId());
			readStatusRepository.save(readStatus);
		}

		return CreateChannelResponse.successWithMembers(channel, uniqueMemberIds);
	}

	@Override
	public JoinChannelResponse joinChannel(JoinChannelRequest request) {
		Channel channel = channelRepository.findByName(request.getChannelName())
			.orElseThrow(ChannelNotFoundException::new);

		if (channel.getMemberIds().contains(request.getUserId())) {
			throw new AlreadyExistsChannelMemberException();
		}

		channel.addUser(request.getUserId());
		channel.addNickname(request.getUserId(), request.getUserDefaultNickname());
		channelRepository.save(channel);

		return JoinChannelResponse.success(channel, request.getUserId(), request.getUserDefaultNickname());
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
	public List<String> getMemberNicknames(String channelName) {
		Channel channel = channelRepository.findByName(channelName)
			.orElseThrow(ChannelNotFoundException::new);
		return new ArrayList<>(channel.getUserNicknames().values());
	}

	@Override
	public List<ChannelResponse> getChannelsByUserId(GetChannelsByUserRequest request) {
		UUID userId = request.getUserId();
		List<ChannelResponse> channelResponseList = new ArrayList<>();

		List<Channel> publicChannels = channelRepository.findAll()
			.stream()
			.filter(channel -> "PUBLIC".equals(channel.getType()))
			.toList();

		for (Channel channel : publicChannels) {
			Instant lastMessageTime = getLastMessageTime(channel.getId());
			channelResponseList.add(ChannelResponse.fromPublicChannel(channel, lastMessageTime));
		}

		List<ReadStatus> userReadStatuses = readStatusRepository.findByUserId(userId);

		for (ReadStatus readStatus : userReadStatuses) {
			Channel channel = channelRepository.findById(readStatus.getChannelId())
				.orElse(null);

			if (channel != null && "PRIVATE".equals(channel.getType())) {
				Instant lastMessageTime = getLastMessageTime(channel.getId());
				List<UUID> participantIds = getPrivateChannelParticipants(channel.getId());
				channelResponseList.add(ChannelResponse.fromPrivateChannel(channel, lastMessageTime, participantIds));
			}
		}

		return channelResponseList;
	}


	// 수정된 updateChannelName 메서드
	@Override
	public ChannelResponse updateChannelName(UpdateChannelnameRequest request) {
		Channel channel = channelRepository.findById(request.getChannelId())
			.orElseThrow(ChannelNotFoundException::new);

		if (channelRepository.existsByName(request.getChannelNewName())) {
			throw new DuplicateChannelNameException();
		}

		if (channel.getType().equals("PRIVATE")) {
			throw new PrivateChannelUpdateException();
		}

		channel.updateChannelName(request.getChannelNewName());
		channel.updateUpdatedAt();
		channelRepository.save(channel);

		return createChannelByType(channel);
	}

	// 수정된 updateUserNickname 메서드
	@Override
	public ChannelResponse updateUserNickname(UpdateUserNicknameRequest request) {
		Channel channel = channelRepository.findById(request.getChannelId())
			.orElseThrow(ChannelNotFoundException::new);

		if (!channel.getMemberIds().contains(request.getUserId())) {
			throw new NotChannelMemberException();
		}

		if (channel.getType().equals("PRIVATE")) {
			throw new PrivateChannelUpdateException();
		}

		channel.addNickname(request.getUserId(), request.getNewNickname());
		channel.updateUpdatedAt();
		channelRepository.save(channel);

		return createChannelByType(channel);
	}

	@Override
	public LeaveChannelResponse leaveChannel(LeaveChannelRequest request) {
		Channel channel = channelRepository.findById(request.getChannelId())
			.orElseThrow(ChannelNotFoundException::new);

		if (!channel.getMemberIds().contains(request.getUserId())) {
			throw new NotChannelMemberException();
		}

		channel.removeUser(request.getUserId());
		channel.removeNickname(request.getUserId());
		channel.updateUpdatedAt();
		channelRepository.save(channel);
		readStatusRepository.deleteByUserIdAndChannelId(request.getUserId(), request.getChannelId());

		return LeaveChannelResponse.success(channel, request.getUserId(), request.getUserDafultNickname());
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
