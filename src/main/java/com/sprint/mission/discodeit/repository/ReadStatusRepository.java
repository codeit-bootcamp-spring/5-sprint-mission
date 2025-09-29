package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sprint.mission.discodeit.domain.entity.ReadStatus;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

	Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

	@Query("""
	  SELECT rs
	  FROM ReadStatus rs
	  JOIN FETCH rs.channel c
	  JOIN FETCH rs.user u
	  LEFT JOIN FETCH u.profileImage
	  WHERE c.id IN :channelIds
	  """)
	List<ReadStatus> findReadStatusDetailAllByChannelIds(@Param("channelIds") List<UUID> channelIds);

	List<ReadStatus> findAllByChannelId(UUID channelId);

	@Query("""
	  SELECT rs
	  FROM ReadStatus rs
	  JOIN FETCH rs.channel c
	  JOIN FETCH rs.user u
	  LEFT JOIN FETCH u.profileImage
	  WHERE rs.id = :id
	  """)
	Optional<ReadStatus> findReadStatusDetailsById(@Param("id") UUID id);

	@Query("""
	      SELECT rs
	      FROM ReadStatus rs
	      JOIN FETCH rs.channel c
	      JOIN FETCH rs.user u
	      LEFT JOIN FETCH u.profileImage
	      WHERE u.id = :userId
	  """)
	List<ReadStatus> findAllByUserId(@Param("userId") UUID userId);

	void deleteByChannelId(UUID channelId);

}
