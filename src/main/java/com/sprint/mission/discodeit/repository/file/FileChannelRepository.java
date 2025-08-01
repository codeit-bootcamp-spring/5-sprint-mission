package com.sprint.mission.discodeit.repository.file;

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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.FileRepository;

@Repository
public class FileChannelRepository implements ChannelRepository, FileRepository {
	private static final String DATA_DIR = "data/";
	private static final String CHANNELS_FILE = DATA_DIR + "channels";
	private static final String CHANNEL_MAPPING_FILE = DATA_DIR + "channelMapping";

	private final Map<UUID, Channel> channelMap;
	private final Map<String, UUID> channelNameToUUID;

	public FileChannelRepository() {
		channelMap = new ConcurrentHashMap<>();
		channelNameToUUID = new ConcurrentHashMap<>();

		createDirectoryIfNotExists();
		loadFile(CHANNELS_FILE, channelMap);
		loadFile(CHANNEL_MAPPING_FILE, channelNameToUUID);

	}

	@Override
	public void save(Channel channel) {
		if (channel == null || channel.getId() == null) {
			return;
		}

		channelMap.put(channel.getId(), channel);
		channelNameToUUID.put(channel.getChannelName(), channel.getId());

		saveFile(CHANNELS_FILE, channelMap);
		saveFile(CHANNEL_MAPPING_FILE, channelNameToUUID);
	}

	@Override
	public Optional<Channel> findById(UUID channelId) {
		return Optional.ofNullable(channelMap.get(channelId)).map(Channel::copy);
	}

	@Override
	public Optional<Channel> findByName(String channelName) {
		UUID channelId = channelNameToUUID.get(channelName);
		if(channelId == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(channelMap.get(channelId)).map(Channel::copy);
	}

	@Override
	public List<Channel> findAll() {
		List<Channel> channelList = new ArrayList<>();
		for (Channel channel : channelMap.values()) {
			channelList.add(channel.copy());
		}
		return channelList.stream().sorted(Comparator.comparing(Channel::getChannelName)).toList();
	}

	@Override
	public boolean existsByName(String channelName) {
		if (channelName == null) return false;
		return channelNameToUUID.containsKey(channelName);
	}

	@Override
	public void deleteById(UUID channelId) {
		if (channelId == null) {
			return;
		}

		Channel channel = channelMap.get(channelId);
		if (channel != null) {
			channelNameToUUID.remove(channel.getChannelName());
			channelMap.remove(channelId);

			saveFile(CHANNELS_FILE, channelMap);
			saveFile(CHANNEL_MAPPING_FILE, channelNameToUUID);
		}
	}

	@Override
	public void deleteByName(String channelName) {
		if (channelName == null) {
			return;
		}

		UUID channelId = channelNameToUUID.get(channelName);
		if (channelId != null) {
			channelMap.remove(channelId);
			channelNameToUUID.remove(channelName);

			saveFile(CHANNELS_FILE, channelMap);
			saveFile(CHANNEL_MAPPING_FILE, channelNameToUUID);
		}
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
}
