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

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

@Repository
@ConditionalOnProperty(
  prefix = "discodeit.repository",
  name = "type",
  havingValue = "file"
)
public class FileUserRepository implements UserRepository {

	private final String FILE_NAME;

	public FileUserRepository(@Value("${discodeit.repository.file-directory}") String fileDirectory) {
		this.FILE_NAME = fileDirectory + "/user.ser";

		try {
			Path filePath = Paths.get(FILE_NAME);
			Files.createDirectories(filePath.getParent());
			if (!Files.exists(filePath)) {
				try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
					 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
					oos.writeObject(new ArrayList<User>());
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public User save(User user) {
		String username = user.getUsername();
		List<User> users = findAll();
		users.removeIf(u -> u.getId().equals(user.getId())); // 중복된 ID 제거

		if (users.stream().anyMatch(u -> u.getUsername().equals(username))) {
			throw new IllegalArgumentException("Username already exists");
		}

		users = new ArrayList<>(users);
		users.add(user);

		try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(users);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return user;
	}

	@Override
	public void delete(UUID userId) {
		List<User> users = findAll();
		int beforeSize = users.size();
		users.removeIf(user -> user.getId().equals(userId));

		if (users.size() == beforeSize) {
			throw new IllegalArgumentException("User with ID " + userId + " not found");
		}

		try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(users);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Optional<User> find(UUID userId) {
		return findAll().stream()
		  .filter(user -> user.getId().equals(userId))
		  .findFirst();
	}

	@Override
	public List<User> findAll() {
		try (FileInputStream fis = new FileInputStream(FILE_NAME);
			 ObjectInputStream ois = new ObjectInputStream(fis)) {
			Object obj = ois.readObject();
			if (obj instanceof List) {
				return (List<User>)obj;
			}
		} catch (Exception e) {
			// 파일이 없거나 읽기 실패 시 빈 리스트 반환
		}

		return List.of();
	}

	@Override
	public boolean isEmpty(UUID userId) {
		return findAll().stream()
		  .noneMatch(user -> user.getId().equals(userId));
	}

	@Override
	public void deleteAll() {
		try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(new ArrayList<User>());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public Long count() {
		return findAll().isEmpty() ? 0L : (long)findAll().size();
	}

	@Override
	public Optional<User> findByUsername(String username) {
		return findAll().stream().filter(user -> user.getUsername().equals(username)).findFirst();
	}

	@Override
	public Optional<User> findByEmail(String email) {
		System.out.println("???");
		findAll().forEach(System.out::println);
		return findAll().stream().filter(user -> user.getEmail().equals(email)).findFirst();
	}
}
