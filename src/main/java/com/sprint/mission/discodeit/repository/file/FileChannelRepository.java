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
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.FileRepository;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileChannelRepository implements ChannelRepository {
	private final String DATA_DIR;
	private final String EXTENSION = ".ser";
	private final String CHANNELS_FILE;
	private final String CHANNEL_MAPPING_FILE;

	private final Map<UUID, Channel> channelMap;
	private final Map<String, UUID> channelNameToUUID;

	public FileChannelRepository(@Value("${discodeit.repository.file-directory:.discodeit}") String fileDirectory) {
		channelMap = new ConcurrentHashMap<>();
		channelNameToUUID = new ConcurrentHashMap<>();

		this.DATA_DIR = fileDirectory.endsWith("/") ? fileDirectory : fileDirectory + "/";
		this.CHANNELS_FILE = DATA_DIR + "channels" + EXTENSION;
		this.CHANNEL_MAPPING_FILE = DATA_DIR + "channelMapping" + EXTENSION;

		createDirectoryIfNotExists();
		loadFile();
	}

	@Override
	public void save(Channel channel) {
		if (channel == null || channel.getId() == null) {
			return;
		}

		Channel existingChannel = channelMap.get(channel.getId());
		if (existingChannel != null) {
			channelNameToUUID.remove(existingChannel.getChannelName());
		}

		channelMap.put(channel.getId(), channel);
		channelNameToUUID.put(channel.getChannelName(), channel.getId());

		saveFile();
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

			saveFile();
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

			saveFile();
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
	public void loadFile() {
		Map<UUID, Channel> tempChannelMap = null;
		Map<String, UUID> tempChannelNameToUUID = null;

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CHANNELS_FILE))) {
			tempChannelMap = (Map<UUID, Channel>) ois.readObject();
		} catch (Exception e) {
			return;
			// throw new RuntimeException("채널 파일 읽기 실패", e);
		}

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CHANNEL_MAPPING_FILE))) {
			tempChannelNameToUUID = (Map<String, UUID>) ois.readObject();
		} catch (Exception e) {
			return;
			// throw new RuntimeException("채널 캐시 파일 읽기 실패", e);
		}

			channelMap.clear();
			channelMap.putAll(tempChannelMap);
			channelNameToUUID.clear();
			channelNameToUUID.putAll(tempChannelNameToUUID);
	}

	@Override
	public void saveFile() {

		Path channelsTmp = Paths.get(CHANNELS_FILE + ".tmp");
		Path channelMappingTmp = Paths.get(CHANNEL_MAPPING_FILE + ".tmp");

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(channelsTmp.toFile()))) {
			oos.writeObject(channelMap);
		} catch (Exception e) {
			throw new RuntimeException("채널 임시 저장 파일 생성 실패", e);
		}

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(channelMappingTmp.toFile()))) {
			oos.writeObject(channelNameToUUID);
		} catch (Exception e) {
			throw new RuntimeException("채널 캐시 임시 저장 파일 생성 실패", e);
		}

		try {
			Files.move(channelsTmp, Paths.get(CHANNELS_FILE), StandardCopyOption.REPLACE_EXISTING);
			Files.move(channelMappingTmp, Paths.get(CHANNEL_MAPPING_FILE), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("채널 파일 복사 실패", e);
		}

		try {
			Files.deleteIfExists(channelsTmp);
			Files.deleteIfExists(channelMappingTmp);
		} catch (IOException e) {
			throw new RuntimeException("임시 파일 삭제 실패", e);
		}

	}
}
