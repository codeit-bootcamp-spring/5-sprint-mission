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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.FileRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;

public class FileMessageRepository implements MessageRepository, FileRepository {
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
			return;
		}
		if (message.getId() == null) {
			return;
		}

		messageMap.put(message.getId(), message);
		saveFile(MESSAGES_FILE, messageMap);
	}

	@Override
	public Optional<Message> findById(UUID messageId) {
		return Optional.ofNullable(messageMap.get(messageId)).map(Message::copy);
	}

	@Override
	public List<Message> findAll() {
		List<Message> messageList = new ArrayList<>();
		for (Message message : messageMap.values()) {
			messageList.add(message.copy());
		}
		return messageList;
	}

	@Override
	public List<Message> findByChannelId(UUID channelId) {
		if (channelId == null) {
			return new ArrayList<>();
		}

		List<Message> messages = new ArrayList<>();
		for (Message message : messageMap.values()) {
			if (channelId.equals(message.getChannelUUID())) {
				messages.add(message.copy());
			}
		}
		return messages;
	}

	@Override
	public void deleteById(UUID messageId) {
		if (messageId == null) {
			return;
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
