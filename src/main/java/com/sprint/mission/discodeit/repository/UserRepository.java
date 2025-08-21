package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.domain.entity.User;

@Repository
public interface UserRepository {

	User save(User user);

	void delete(UUID userId);

	Optional<User> find(UUID userId);

	List<User> findAll();

	boolean isEmpty(UUID userId);

	void deleteAll();

	Long count(); // 추가된 메소드: 전체 사용자 수를 반환하는 메소드

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

}
