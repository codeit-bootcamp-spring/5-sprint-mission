package com.sprint.mission.discodeit.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {

	Optional<User> findByUsername(String username);

	List<User> findAllByIdIn(Collection<UUID> ids);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	List<User> findByRole(Role role);
}
