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
import com.sprint.mission.discodeit.dto.request.channel.UpdateUserNicknameRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.CreateChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.DeleteChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.JoinChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.LeaveChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.AlreadyExistsChannelMemberException;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.DuplicateChannelNameException;
import com.sprint.mission.discodeit.exception.NotChannelMemberException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
	private final ChannelRepository channelRepository;
	private final ReadStatusRepository readStatusRepository;
	private final MessageRepository messageRepository;

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
		Channel channel = new Channel(request.getMemberIds());

		channelRepository.save(channel);

		for (UUID userId : request.getMemberIds()) {
			ReadStatus readStatus = new ReadStatus(userId, channel.getId());
			readStatusRepository.save(readStatus);
		}

		return CreateChannelResponse.successWithMembers(channel, request.getMemberIds());
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

	@Override
	public boolean updateChannelName(UUID channelUUID, String channelNewName) {
		Channel channel = channelRepository.findById(channelUUID)
			.orElseThrow(ChannelNotFoundException::new);

		if (channelRepository.existsByName(channelNewName)) {
			throw new DuplicateChannelNameException();
		}

		channel.updateChannelName(channelNewName);
		channel.updateUpdatedAt();
		channelRepository.save(channel);

		return true;
	}

	@Override
	public boolean updateUserNickname(UpdateUserNicknameRequest request) {
		Channel channel = channelRepository.findById(request.getChannelId())
			.orElseThrow(ChannelNotFoundException::new);

		if (!channel.getMemberIds().contains(request.getUserId())) {
			throw new NotChannelMemberException();
		}

		channel.addNickname(request.getUserId(), request.getNewNickname());
		channel.updateUpdatedAt();
		channelRepository.save(channel);

		return true;
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

	// @Deprecated 레거시 코드

	@Override
	public Channel createChannel(String channelName) {
		return null;
	}

	@Override
	public boolean joinChannel(User user, String channelName) {
		return false;
	}

	@Override
	public Channel getChannelByName(String channelName) {
		return null;
	}

	@Override
	public Channel getChannelByUUID(UUID channelUUID) {
		return null;
	}

	@Override
	public List<Channel> getAllChannels() {
		return List.of();
	}

	@Override
	public boolean updateUserNickname(UUID channelUUID, UUID userUUID, String newNickname) {
		return false;
	}

	@Override
	public boolean leaveChannel(UUID channelUUID, UUID userUUID) {
		return false;
	}

	@Override
	public boolean deleteChannel(UUID channelUUID) {
		return false;
	}

	@Override
	public boolean deleteChannel(String channelName) {
		return false;
	}
}
