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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.FileService;

public class FileMessageRepository implements MessageRepository, FileService {
	private static final String DATA_DIR = "data/";
	private static final String MESSAGES_FILE = DATA_DIR + "messages";

	private final Map<UUID, Message> messageMap;

	public FileMessageRepository() {
		messageMap = new ConcurrentHashMap<>();

		createDirectoryIfNotExists();
		loadFile(MESSAGES_FILE, messageMap);
	}

	@Override
	public void save(Message message) {
		if (message == null) {
			throw new IllegalArgumentException("null!!");
		}
		if (message.getId() == null) {
			throw new IllegalArgumentException("null!!");
		}

		messageMap.put(message.getId(), message);
		saveFile(MESSAGES_FILE, messageMap);
	}

	@Override
	public Message findById(UUID messageId) {
		if (messageId == null) {
			throw new IllegalArgumentException("null!!");
		}

		return messageMap.get(messageId);
	}

	@Override
	public List<Message> findAll() {
		return new ArrayList<>(messageMap.values());
	}

	@Override
	public List<Message> findByChannelId(UUID channelId) {
		if (channelId == null) {
			throw new IllegalArgumentException("null!!");
		}

		return messageMap.values().stream()
			.filter(message -> channelId.equals(message.getChannelUUID()))
			.collect(Collectors.toList());
	}

	@Override
	public List<Message> findByAuthorId(UUID authorId) {
		if (authorId == null) {
			throw new IllegalArgumentException("null!!");
		}

		return messageMap.values().stream()
			.filter(message -> authorId.equals(message.getAuthorUUID()))
			.collect(Collectors.toList());
	}

	@Override
	public List<Message> findByChannelIdAndAuthorId(UUID channelId, UUID authorId) {
		if (channelId == null || authorId == null) {
			throw new IllegalArgumentException("null!!");
		}

		return messageMap.values().stream()
			.filter(message -> channelId.equals(message.getChannelUUID()) &&
				authorId.equals(message.getAuthorUUID()))
			.collect(Collectors.toList());
	}

	@Override
	public void deleteById(UUID messageId) {
		if (messageId == null) {
			throw new IllegalArgumentException("null!!");
		}

		messageMap.remove(messageId);
		saveFile(MESSAGES_FILE, messageMap);
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
			Map load = (Map)ois.readObject();
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
