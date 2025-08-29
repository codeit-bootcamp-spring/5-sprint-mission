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

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

@Repository
@ConditionalOnProperty(
  prefix = "discodeit.repository",
  name = "type",
  havingValue = "file"
)
public class FileChannelRepository implements ChannelRepository {

	private final String FILE_NAME;

	public FileChannelRepository(@Value("${discodeit.repository.file-directory}") String fileDirectory) {
		this.FILE_NAME = fileDirectory + "/channel.ser";
		try {
			Path filePath = Paths.get(FILE_NAME);
			Files.createDirectories(filePath.getParent());
			if (!Files.exists(filePath)) {
				try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
					 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
					oos.writeObject(new ArrayList<Channel>());
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Channel save(Channel channel) {

		List<Channel> channels = new ArrayList<>(findAll());
		channels.removeIf(c -> c.getId().equals(channel.getId())); // 기존 id 삭제
		channels = new ArrayList<>(channels);
		channels.add(channel);

		try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(channels);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return channel;
	}

	@Override
	public Optional<Channel> find(UUID id) {
		List<Channel> channels = findAll();
		return channels.stream()
		  .filter(channel -> channel.getId().equals(id))
		  .findFirst();
	}

	@Override
	public List<Channel> findAll() {
		try (FileInputStream fis = new FileInputStream(FILE_NAME);
			 ObjectInputStream ois = new ObjectInputStream(fis)) {
			Object obj = ois.readObject();
			if (obj instanceof List) {
				return (List<Channel>)obj;
			}
		} catch (Exception e) {
			// 파일이 없거나 읽기 실패 시 빈 리스트 반환
		}
		return List.of();
	}

	@Override
	public void delete(UUID id) {
		List<Channel> channels = findAll();
		int beforeSize = channels.size();
		channels.removeIf(channel -> channel.getId().equals(id));

		if (channels.size() == beforeSize) {
			throw new IllegalArgumentException("해당 id의 Channel이 없습니다: " + id);
		}

		try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(channels);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean existsById(UUID id) {
		return findAll().stream().anyMatch(channel -> channel.getId().equals(id));
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
	public Long count() {
		return findAll().isEmpty() ? 0L : (long)findAll().size();
	}
}
