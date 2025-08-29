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

import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

@Repository
@ConditionalOnProperty(
  prefix = "discodeit.repository",
  name = "type",
  havingValue = "file"
)
public class FileReadStatusRepository implements ReadStatusRepository {

	private final String FILE_NAME;

	public FileReadStatusRepository(@Value("${discodeit.repository.file-directory}") String fileDirectory) {
		this.FILE_NAME = fileDirectory + "/readStatus.ser";

		try {
			Path filePath = Paths.get(FILE_NAME);
			Files.createDirectories(filePath.getParent());
			if (!Files.exists(filePath)) {
				try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
					 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
					oos.writeObject(new ArrayList<ReadStatus>());
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ReadStatus save(ReadStatus readStatus) {
		List<ReadStatus> binaryContents = findAll();

		binaryContents = new ArrayList<>(binaryContents);
		binaryContents.removeIf(existingStatus -> existingStatus.getId().equals(readStatus.getId()));
		binaryContents.add(readStatus);

		try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(binaryContents);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return readStatus;
	}

	@Override
	public Optional<ReadStatus> find(UUID id) {
		return findAll().stream()
		  .filter(binaryContent -> binaryContent.getId().equals(id))
		  .findFirst();
	}

	@Override
	public List<ReadStatus> findAllByUserId(UUID userId) {
		return findAll().stream()
		  .filter(binaryContent -> binaryContent.getUser().getId().equals(userId)).toList();
	}

	@Override
	public List<ReadStatus> findAllByChannelId(UUID channelId) {
		return findAll().stream()
		  .filter(binaryContent -> binaryContent.getChannel().getId().equals(channelId)).toList();
	}

	@Override
	public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
		return findAll().stream()
		  .filter(binaryContent -> binaryContent.getUser().getId().equals(userId))
		  .filter(binaryContent -> binaryContent.getChannel().getId().equals(channelId))
		  .findFirst();
	}

	@Override
	public List<ReadStatus> findAll() {
		try (FileInputStream fis = new FileInputStream(FILE_NAME);
			 ObjectInputStream ois = new ObjectInputStream(fis)) {
			Object obj = ois.readObject();
			if (obj instanceof List) {
				return (List<ReadStatus>)obj;
			}
		} catch (Exception e) {
			// 파일이 없거나 읽기 실패 시 빈 리스트 반환
		}

		return List.of();
	}

	@Override
	public void delete(UUID id) {
		List<ReadStatus> binaryContents = findAll();
		binaryContents.removeIf(binaryContent -> binaryContent.getId().equals(id));

		try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(binaryContents);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public boolean isEmpty(UUID id) {
		return findAll().stream()
		  .noneMatch(binaryContent -> binaryContent.getId().equals(id));
	}

	@Override
	public void deleteByChannelId(UUID channelId) {
		List<ReadStatus> readStatuses = findAll();
		readStatuses.removeIf(readStatus -> readStatus.getChannel().getId().equals(channelId));

		try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(readStatuses);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteAll() {
		try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(new ArrayList<BinaryContent>());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
