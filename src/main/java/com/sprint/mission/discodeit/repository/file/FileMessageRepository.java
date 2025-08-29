package com.sprint.mission.discodeit.repository.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.domain.entity.Messages;
import com.sprint.mission.discodeit.repository.MessageRepository;

@Repository
@ConditionalOnProperty(
  prefix = "discodeit.repository",
  name = "type",
  havingValue = "file"
)
public class FileMessageRepository implements MessageRepository {

	private final String FILE_NAME;

	public FileMessageRepository(@Value("${discodeit.repository.file-directory}") String fileDirectory) {
		this.FILE_NAME = fileDirectory + "/message.ser";

		try {
			Path filePath = Paths.get(FILE_NAME);
			Files.createDirectories(filePath.getParent());
			if (!Files.exists(filePath)) {
				try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
					 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
					oos.writeObject(new ArrayList<Messages>());
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Messages save(Messages message) {
		List<Messages> messages = new ArrayList<>(findAll());
		// 중복된 ID가 있을 경우 제거
		messages.removeIf(existingMessage -> existingMessage.getId().equals(message.getId()));
		messages.add(message);

		try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(messages);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return message;
	}

	@Override
	public void delete(UUID id) {
		List<Messages> messages = findAll();
		int beforeSize = messages.size();
		messages.removeIf(message -> message.getId().equals(id));

		if (messages.size() == beforeSize) {
			throw new IllegalArgumentException("Message with ID " + id + " not found");
		}

		try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(messages);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteAll() {
		try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(List.of()); // 빈 리스트로 덮어쓰기
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteByChannelId(UUID channelId) {
		List<Messages> messages = new ArrayList<>(findAll());
		int beforeSize = messages.size();
		messages.removeIf(message -> message.getChannels().getId().equals(channelId));

		if (messages.size() == beforeSize) {
			return; // 채널 ID에 해당하는 메시지가 없으면 아무 작업도 하지 않음
			// throw new IllegalArgumentException("No messages found for channel ID " + channelId);
		}

		try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(messages);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public Optional<Messages> find(UUID id) {
		return findAll().stream()
		  .filter(message -> message.getId().equals(id))
		  .findFirst();
	}

	@Override
	public List<Messages> findAll() {
		try (FileInputStream fis = new FileInputStream(FILE_NAME);
			 ObjectInputStream ois = new ObjectInputStream(fis)) {
			Object obj = ois.readObject();
			if (obj instanceof List) {
				return (List<Messages>)obj;
			}
		} catch (Exception e) {
			// 파일이 없거나 읽기 실패 시 빈 리스트 반환
		}
		return List.of();
	}

	@Override
	public List<Messages> findAllByChannelId(UUID channelId) {
		return findAll().stream()
		  .filter(message -> message.getChannels().getId().equals(channelId))
		  .toList();
	}

	@Override
	public boolean isEmpty(UUID messageId) {
		return findAll().stream()
		  .noneMatch(message -> message.getId().equals(messageId));
	}

	@Override
	public Long count() {
		return findAll().isEmpty() ? 0L : (long)findAll().size();
	}
}
