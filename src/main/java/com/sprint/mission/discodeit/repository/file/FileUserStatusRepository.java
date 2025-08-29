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

import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

@Repository
@ConditionalOnProperty(
  prefix = "discodeit.repository",
  name = "type",
  havingValue = "file"
)
public class FileUserStatusRepository implements UserStatusRepository {

	private final String FILE_NAME;

	public FileUserStatusRepository(@Value("${discodeit.repository.file-directory}") String fileDirectory) {
		this.FILE_NAME = fileDirectory + "/userStatus.ser";

		try {
			Path filePath = Paths.get(FILE_NAME);
			Files.createDirectories(filePath.getParent());
			if (!Files.exists(filePath)) {
				try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
					 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
					oos.writeObject(new ArrayList<UserStatus>());
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public UserStatus save(UserStatus userStatus) {
		List<UserStatus> userStatusList = new ArrayList<>(findAll());

		userStatusList.removeIf(u -> u.getId().equals(userStatus.getId())); // 중복된 ID 제거
		userStatusList.add(userStatus);

		try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(userStatusList);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return userStatus;
	}

	@Override
	public Optional<UserStatus> find(UUID id) {
		return findAll().stream().filter(u -> u.getId().equals(id)).findFirst();
	}

	@Override
	public Optional<UserStatus> findByUserId(UUID userId) {
		return findAll().stream()
		  .filter(u -> u.getUser().getId().equals(userId))
		  .findFirst();
	}

	@Override
	public java.util.List<UserStatus> findAll() {
		try (FileInputStream fis = new FileInputStream(FILE_NAME);
			 ObjectInputStream ois = new ObjectInputStream(fis)) {
			Object obj = ois.readObject();
			if (obj instanceof List) {
				return (List<UserStatus>)obj;
			}
		} catch (Exception e) {
			// 파일이 없거나 읽기 실패 시 빈 리스트 반환
		}

		return List.of();
	}

	@Override
	public void delete(UUID id) {
		List<UserStatus> userStatusList = new ArrayList<>(findAll());
		int beforeSize = userStatusList.size();
		userStatusList.removeIf(u -> u.getId().equals(id));

		if (userStatusList.size() == beforeSize) {
			throw new IllegalArgumentException("UserStatus with ID " + id + " not found");
		}

		try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(userStatusList);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isEmpty(UUID id) {
		return findAll().stream().noneMatch(u -> u.getId().equals(id));
	}

	@Override
	public void deleteAll() {
		try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(new ArrayList<UserStatus>());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteByUserId(UUID userId) {
		List<UserStatus> userStatusList = new ArrayList<>(findAll());
		userStatusList.removeIf(u -> u.getUser().getId().equals(userId));

		try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(userStatusList);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
