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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.FileService;

public class FileChannelService implements ChannelService, FileService {
	private static final String DATA_DIR = "data/";
	private static final String CHANNELS_FILE = DATA_DIR + "channels";
	private static final String CHANNEL_MAPPING_FILE = DATA_DIR + "channelMapping";

	private final Map<UUID, Channel> channelMap;
	private final Map<String, UUID> channelNameToUUID;

	public FileChannelService() {
		channelMap = new ConcurrentHashMap<>();
		channelNameToUUID = new ConcurrentHashMap<>();

		createDirectoryIfNotExists();
		loadFile(CHANNELS_FILE, channelMap);
		loadFile(CHANNEL_MAPPING_FILE, channelNameToUUID);
	}

	@Override
	public Channel createChannel(String channelName) {
		if(channelNameToUUID.containsKey(channelName)) return null;
		Channel channel = new Channel(channelName);

		channelMap.put(channel.getId(), channel);
		channelNameToUUID.put(channelName, channel.getId());

		saveFile(CHANNELS_FILE, channelMap);
		saveFile(CHANNEL_MAPPING_FILE, channelNameToUUID);

		return channel;
	}

	@Override
	public boolean joinChannel(User user, String channelName) {
		if(!channelNameToUUID.containsKey(channelName)) return false;

		Channel channel = channelMap.get(channelNameToUUID.get(channelName));
		channel.addUser(user.getId());
		channel.addNickname(user.getId(), user.getDefaultNickname());

		saveFile(CHANNELS_FILE, channelMap);

		return true;
	}

	@Override
	public Channel findChannel(String channelName) {
		if(!channelNameToUUID.containsKey(channelName)) return null;
		return channelMap.get(channelNameToUUID.get(channelName));
	}

	@Override
	public Channel findChannel(UUID channelUUID) {
		return channelMap.get(channelUUID);
	}

	@Override
	public List<String> findChannelMemberNickname(String channelName){
		if (channelName == null || !channelNameToUUID.containsKey(channelName)) return new ArrayList<>();

		Channel channel = channelMap.get(channelNameToUUID.get(channelName));
		if (channel == null) return new ArrayList<>();

		List<String> nicknameList = new ArrayList<>(channel.getUserNicknames().values());
		nicknameList.sort((n1, n2) -> n1.compareTo(n2));

		return nicknameList;
	}

	@Override
	public List<Channel> findChannelAll() {
		List<Channel> channelList = new ArrayList<>(channelMap.values());
		channelList.sort((c1, c2) -> c1.getChannelName().compareTo(c2.getChannelName()));
		return channelList;
	}

	@Override
	public boolean updateChannelName(User user, UUID channelUUID, String channelNewName) {
		// 새로운 이름이 이미 사용 중이거나 업데이트 할 채널이 없을 경우 return false
		if(channelNameToUUID.containsKey(channelNewName) ||
			!channelMap.containsKey(channelUUID)){
			return false;
		}

		Channel channel = channelMap.get(channelUUID);
		channel.updateChannelName(channelNewName);
		channel.updateUpdatedAt();

		deleteChannel(channelUUID);

		channelMap.put(channelUUID, channel);
		channelNameToUUID.put(channelNewName, channel.getId());

		saveFile(CHANNELS_FILE, channelMap);
		saveFile(CHANNEL_MAPPING_FILE, channelNameToUUID);

		return true;
	}

	@Override
	public boolean updateUserNickname(UUID channelUUID, UUID userUUID, String newNickname) {
		if (newNickname == null || newNickname.isEmpty()) return false;

		Channel channel = findChannel(channelUUID);
		if (channel == null) return false;

		channel.addNickname(userUUID, newNickname);
		channel.updateUpdatedAt();

		saveFile(CHANNELS_FILE, channelMap);
		return true;
	}

	@Override
	public boolean leaveChannel(UUID channelUUID, UUID userUUID) {
		if(!channelMap.containsKey(channelUUID)) return false;
		channelMap.get(channelUUID).removeUser(userUUID);
		channelMap.get(channelUUID).removeNickname(userUUID);

		saveFile(CHANNELS_FILE, channelMap);

		return true;
	}

	@Override
	public boolean deleteChannel(UUID channelUUID) {
		if(!channelMap.containsKey(channelUUID)) return false;

		String channelName = channelMap.get(channelUUID).getChannelName();

		channelMap.remove(channelUUID);
		channelNameToUUID.remove(channelName);

		saveFile(CHANNELS_FILE, channelMap);
		saveFile(CHANNEL_MAPPING_FILE, channelNameToUUID);

		return true;
	}

	@Override
	public boolean deleteChannel(String channelName) {
		if(!channelNameToUUID.containsKey(channelName)) return false;

		UUID channelUUID = channelNameToUUID.get(channelName);

		channelMap.remove(channelUUID);
		channelNameToUUID.remove(channelName);

		saveFile(CHANNELS_FILE, channelMap);
		saveFile(CHANNEL_MAPPING_FILE, channelNameToUUID);

		return true;
	}

	@Override
	public void createDirectoryIfNotExists() {
		try {
			Path dataPath = Paths.get(DATA_DIR);
			if (!Files.exists(dataPath)) {
				Files.createDirectories(dataPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadFile(String filename, Map map) {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
			Map load = (Map) ois.readObject();
			map.putAll(load);
		} catch (FileNotFoundException ignored) {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveFile(String filename, Object data) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
			oos.writeObject(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveChannelData() {
		saveFile(CHANNELS_FILE, channelMap);
	}

}
