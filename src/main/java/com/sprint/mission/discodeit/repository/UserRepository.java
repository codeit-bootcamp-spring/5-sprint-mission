package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.domain.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

	Optional<User> findByUsername(String username);

	@Query("SELECT u FROM User u LEFT JOIN FETCH u.profileImage WHERE u.username = :username")
	Optional<User> findByUsernameWithProfileImage(@Param("username") String username);

	@Query("SELECT u FROM User u LEFT JOIN FETCH u.profileImage WHERE u.id = :id")
	Optional<User> findUserWithProfileImageByID(@Param("id") UUID id);

	Optional<User> findByEmail(String email);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	@Query("SELECT u FROM User u LEFT JOIN FETCH u.profileImage WHERE u.id IN :ids")
	List<User> findUsersWithProfileByIdIn(List<UUID> ids);

	@Query("""
	  	SELECT u
	  	FROM User u
	  	LEFT JOIN FETCH u.profileImage
	  """)
	List<User> findUserDetailsAll();
}
