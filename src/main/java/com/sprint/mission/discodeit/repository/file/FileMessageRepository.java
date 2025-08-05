package com.sprint.mission.discodeit.repository.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

@Repository
public class FileMessageRepository implements MessageRepository {
	private static final String DATA_DIR = "data/";
	private static final String EXTENSION = ".ser";
	private static final String MESSAGES_FILE = DATA_DIR + "messages" + EXTENSION;

	private final Map<UUID, Message> messageMap;

	public FileMessageRepository() {
		messageMap = new ConcurrentHashMap<>();

		createDirectoryIfNotExists();
		loadFile();
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
		saveFile();
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
		saveFile();
	}

	// 데이터 많아지면 역색인 Map 사용 고려하기!
	// 일단 넘어가자 할 게 많다...
	@Override
	public void deleteByChannelId(UUID channelId) {
		if (channelId == null) {
			return;
		}
		List<UUID> deleteMessages = new ArrayList<>();
		for (Message message : messageMap.values()) {
			if (channelId.equals(message.getChannelUUID())) {
				deleteMessages.add(message.getId());
			}
		}
		for (UUID id : deleteMessages) {
			messageMap.remove(id);
		}
		saveFile();
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
		Map<UUID, Message> tempMessageMap = null;
		boolean channelsLoaded = false;

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MESSAGES_FILE))) {
			tempMessageMap = (Map<UUID, Message>)ois.readObject();
			channelsLoaded = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (channelsLoaded) {
			messageMap.clear();
			messageMap.putAll(tempMessageMap);
		}
	}

	@Override
	public void saveFile() {

		Path messagesTmp = Paths.get(MESSAGES_FILE + ".tmp");

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(messagesTmp.toFile()))) {
			oos.writeObject(messageMap);
		} catch (Exception e) {
			throw new RuntimeException("임시 저장 파일 생성 실패", e);
		}

		try {
			Files.move(messagesTmp, Paths.get(MESSAGES_FILE), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("임시 파일 이동 실패", e);
		}

		try {
			Files.deleteIfExists(messagesTmp);
		} catch (IOException e) {
			throw new RuntimeException("임시 파일 삭제 실패", e);
		}

	}
}
