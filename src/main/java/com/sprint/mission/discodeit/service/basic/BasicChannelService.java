package com.sprint.mission.discodeit.service.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

public class BasicChannelService implements ChannelService {
	private final ChannelRepository channelRepository;

	public BasicChannelService(ChannelRepository channelRepository) {
		this.channelRepository = channelRepository;
	}

	@Override
	public Channel createChannel(String channelName) {
		if (channelRepository.existsByName(channelName)) {
			throw new DuplicateChannelNameException();
		}

		Channel newChannel = new Channel(channelName);
		channelRepository.save(newChannel);

		return newChannel;
	}

	@Override
	public boolean joinChannel(User user, String channelName) {
		Channel channel = getChannelByName(channelName);

		if (channel.getChannelUsersUUID().contains(user.getId())) {
			throw new AlreadyChannelMemberException();
		}

		channel.addUser(user.getId());
		channel.addNickname(user.getId(), user.getDefaultNickname());
		channelRepository.save(channel);

		return true;
	}

	@Override
	public Channel getChannelByName(String channelName) {
		Channel channel = channelRepository.findByName(channelName);

		if (channel == null) {
			throw new ChannelNotFoundException();
		}

		return channel;
	}

	@Override
	public Channel getChannelByUUID(UUID channelUUID) {
		Channel channel = channelRepository.findById(channelUUID);
		if (channel == null) {
			throw new ChannelNotFoundException();
		}
		return channel;
	}

	@Override
	public List<String> getMemberNicknames(String channelName) {
		Channel channel = channelRepository.findByName(channelName);
		if (channel == null) {
			throw new ChannelNotFoundException();
		}
		return new ArrayList<>(channel.getUserNicknames().values());
	}

	@Override
	public List<Channel> getAllChannels() {
		return channelRepository.findAll();
	}

	@Override
	public boolean updateChannelName(User user, UUID channelUUID, String channelNewName) {
		Channel channel = getChannelByUUID(channelUUID);

		if (channelRepository.existsByName(channelNewName)) {
			throw new DuplicateChannelNameException();
		}

		channel.updateChannelName(channelNewName);
		channel.updateUpdatedAt();
		channelRepository.save(channel);

		return true;
	}

	@Override
	public boolean updateUserNickname(UUID channelUUID, UUID userUUID, String newNickname) {
		Channel channel = getChannelByUUID(channelUUID);

		if (!channel.getChannelUsersUUID().contains(userUUID)) {
			throw new NotChannelMemberException();
		}

		channel.addNickname(userUUID, newNickname);
		channel.updateUpdatedAt();
		channelRepository.save(channel);

		return true;
	}

	@Override
	public boolean leaveChannel(UUID channelUUID, UUID userUUID) {
		Channel channel = getChannelByUUID(channelUUID);

		if (!channel.getChannelUsersUUID().contains(userUUID)) {
			throw new NotChannelMemberException();
		}

		channel.removeUser(userUUID);
		channel.removeNickname(userUUID);
		channel.updateUpdatedAt();
		channelRepository.save(channel);

		return true;
	}

	@Override
	public boolean deleteChannel(UUID channelUUID) {
		Channel channel = getChannelByUUID(channelUUID);
		channelRepository.deleteById(channelUUID);
		return true;
	}

	@Override
	public boolean deleteChannel(String channelName) {
		Channel channel = getChannelByName(channelName);
		channelRepository.deleteByName(channelName);
		return true;
	}

	// 메시지 연관 저장용 , 혹은 임의로 채널 정보 저장
	public void saveChannel(Channel channel) {
		channelRepository.save(channel);
	}
}
