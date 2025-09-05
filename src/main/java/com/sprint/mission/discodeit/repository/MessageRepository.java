package com.sprint.mission.discodeit.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sprint.mission.discodeit.domain.entity.Message;

public interface MessageRepository extends JpaRepository<Message, UUID> {

	void deleteByChannelId(UUID channelId);

	Page<Message> findAllByChannelId(UUID channelId, Pageable pageable);

	@Query(value = """
	  SELECT m
	  FROM Message m
	  JOIN FETCH m.channel
	  JOIN FETCH m.attachments
	  JOIN FETCH m.user u
	  LEFT JOIN FETCH u.profileImage
	  WHERE m.channel.id = :channelId
	  """,
	  countQuery = """
		  SELECT COUNT(m) FROM Message m WHERE m.channel.id = :channelId
		""")
	Page<Message> findAllDetailsByChannelId(UUID channelId, Pageable pageable);

	@Query("""
	  SELECT m
	  FROM Message m
	  JOIN fetch m.attachments
	  JOIN fetch m.user u
	  JOIN fetch m.channel
	  Left JOIN fetch u.profileImage
	  WHERE m.id = :id
	  """)
	Optional<Message> findMessageDetailsById(@Param("id") UUID id);

	@Query(value = """
	  SELECT m 
	  FROM Message m
	  JOIN FETCH m.attachments
	  JOIN FETCH m.user u
	  LEFT JOIN FETCH u.profileImage
	  WHERE m.channel.id = :channelId
	  """,
	  countQuery = """
		SELECT COUNT(m) 
		FROM Message m
		WHERE m.channel.id = :channelId
		""")
	Page<Message> findAllByChannelIdWithUser(@Param("channelId") UUID channelId, Pageable pageable);
}
