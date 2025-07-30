package com.sprint.mission.discodeit.service.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

public class FileChannelService implements ChannelService {
	private final ChannelRepository channelRepository;

	public FileChannelService(ChannelRepository channelRepository) {
		this.channelRepository = channelRepository;
	}

	@Override
	public Channel createChannel(String channelName) {
		if(channelRepository.existsByName(channelName)) return null;

		Channel channel = new Channel(channelName);

		channelRepository.save(channel);

		return channel;
	}

	@Override
	public boolean joinChannel(User user, String channelName) {
		Optional<Channel> channelOpt = channelRepository.findByName(channelName);
		if (channelOpt.isPresent()) {
			Channel channel = channelOpt.get();
			channel.addUser(user.getId());
			channel.addNickname(user.getId(), user.getDefaultNickname());

			channelRepository.save(channel);
			return true;
		}
		return false;
	}

	@Override
	public Channel getChannelByName(String channelName) {
		return channelRepository.findByName(channelName).orElse(null);
	}

	@Override
	public Channel getChannelByUUID(UUID channelUUID) {
		return channelRepository.findById(channelUUID).orElse(null);
	}

	@Override
	public List<String> getMemberNicknames(String channelName){
		if (channelName == null) return new ArrayList<>();

		Optional<Channel> channelOpt = channelRepository.findByName(channelName);
		if (channelOpt.isPresent()) {
			Channel channel = channelOpt.get();
			List<String> nicknameList = new ArrayList<>(channel.getUserNicknames().values());
			nicknameList.sort((n1, n2) -> n1.compareTo(n2));
			return nicknameList;
		}
		return new ArrayList<>();
	}

	@Override
	public List<Channel> getAllChannels() {
		return channelRepository.findAll();
	}

	@Override
	public boolean updateChannelName(User user, UUID channelUUID, String channelNewName) {
		if(channelRepository.existsByName(channelNewName)) {
			return false;
		}

		Optional<Channel> channelOpt = channelRepository.findById(channelUUID);
		if (channelOpt.isPresent()) {
			Channel channel = channelOpt.get();
			channel.updateChannelName(channelNewName);
			channel.updateUpdatedAt();
			channelRepository.deleteById(channelUUID);
			channelRepository.save(channel);
			return true;
		}
		return false;

	}

	@Override
	public boolean updateUserNickname(UUID channelUUID, UUID userUUID, String newNickname) {
		if (newNickname == null || newNickname.isEmpty()) return false;

		Optional<Channel> channelOpt = channelRepository.findById(channelUUID);
		if (channelOpt.isPresent()) {
			Channel channel = channelOpt.get();
			channel.addNickname(userUUID, newNickname);
			channel.updateUpdatedAt();
			// Before: saveFile(CHANNELS_FILE, channelMap);
			channelRepository.save(channel);
			return true;
		}
		return false;
	}

	@Override
	public boolean leaveChannel(UUID channelUUID, UUID userUUID) {
		Optional<Channel> channelOpt = channelRepository.findById(channelUUID);
		if (channelOpt.isPresent()) {
			Channel channel = channelOpt.get();
			channel.removeUser(userUUID);
			channel.removeNickname(userUUID);
			channelRepository.save(channel);
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteChannel(UUID channelUUID) {
		if (channelRepository.findById(channelUUID).isPresent()) {
			channelRepository.deleteById(channelUUID);
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteChannel(String channelName) {
		if (channelRepository.existsByName(channelName)) {
			channelRepository.deleteByName(channelName);
			return true;
		}
		return false;
	}

	public void saveChannel(Channel channel) {
		channelRepository.save(channel);
	}
}
