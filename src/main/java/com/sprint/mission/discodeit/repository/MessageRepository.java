package com.sprint.mission.discodeit.repository;

import java.time.Instant;
import java.util.List;
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

	@Query(value = """
	  SELECT m
	  FROM Message m
	  JOIN FETCH m.channel
	  JOIN FETCH m.user u
	  LEFT JOIN FETCH m.attachments
	  LEFT JOIN FETCH u.profileImage
	  WHERE m.channel.id = :channelId
	  """)
	Page<Message> findAllDetailsByChannelId(@Param("channelId") UUID channelId, Pageable pageable);

	@Query("""
	      SELECT m.id 
	      FROM Message m 
	      WHERE m.channel.id = :channelId
	      ORDER BY m.createdAt DESC
	  """)
	Page<UUID> findMessageIdsByChannelId(@Param("channelId") UUID channelId, Pageable pageable);

	@Query("""
	      SELECT m
	      FROM Message m
	      JOIN FETCH m.channel
	      JOIN FETCH m.user u
	      LEFT JOIN FETCH m.attachments
	      LEFT JOIN FETCH u.profileImage
	      WHERE m.id IN :messageIds
	  """)
	List<Message> findAllDetailsByIds(@Param("messageIds") List<UUID> messageIds);

	@Query("""
	      SELECT m
	      FROM Message m
	      JOIN FETCH m.channel
	      JOIN FETCH m.attachments
	      JOIN FETCH m.user u
	      LEFT JOIN FETCH u.profileImage
	      WHERE m.channel.id = :channelId
	        AND m.createdAt < :cursor
	      ORDER BY m.createdAt DESC
	  """)
	Page<Message> findAllDetailsByChannelIdAndCursor(
	  @Param("channelId") UUID channelId,
	  @Param("cursor") Instant cursor,
	  Pageable pageable);

	@Query("""
	  SELECT m
	  FROM Message m
	  LEFT JOIN fetch m.attachments
	  JOIN fetch m.user u
	  JOIN fetch m.channel
	  Left JOIN fetch u.profileImage
	  WHERE m.id = :id
	  """)
	Optional<Message> findMessageDetailsById(@Param("id") UUID id);

}
