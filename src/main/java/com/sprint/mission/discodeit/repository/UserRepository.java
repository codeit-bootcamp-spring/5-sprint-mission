package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;

public interface UserRepository {
	void save(User user);
	User findById(UUID id);
	User findByLoginId(String loginId);
	List<User> findAll();
	boolean existsByLoginId(String loginId);
	void deleteById(UUID id);
	void deleteByLoginId(String loginId);
}
