package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;

public interface UserRepository {
	void save(User user);
	Optional<User> findById(UUID id);
	Optional<User> findByLoginId(String loginId);
	Optional<User> findByLoginIdAnaPassword(String loginId, String password);
	List<User> findAll();
	boolean existsById(UUID id);
	boolean existsByLoginId(String loginId);
	boolean existsByEmail(String email);
	void deleteById(UUID id);
	void deleteByLoginId(String loginId);

	void createDirectoryIfNotExists();
	void loadFile();
	void saveFile();
}
