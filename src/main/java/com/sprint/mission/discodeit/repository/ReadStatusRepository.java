package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sprint.mission.discodeit.domain.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.domain.enums.ChannelType;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

	public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

	@Query("""
	  SELECT rs
	  FROM ReadStatus rs
	  JOIN FETCH rs.channel c
	  JOIN FETCH rs.user u
	  WHERE u.id = :userId
	  """)
	public List<ReadStatus> findReadStatusDetailAllByUserId(@Param("userId") UUID userId);

	@Query("""
	  SELECT rs
	  FROM ReadStatus rs
	  JOIN FETCH rs.channel c
	  JOIN FETCH rs.user u
	  LEFT JOIN FETCH u.profileImage
	  WHERE c.id IN :channelIds
	  """)
	List<ReadStatus> findReadStatusDetailAllByChannelIds(@Param("channelIds") List<UUID> channelIds);

	public List<ReadStatus> findAllByChannelId(UUID channelId);

	@Query("""
	  SELECT rs
	  FROM ReadStatus rs
	  JOIN FETCH rs.channel c
	  JOIN FETCH rs.user u
	  LEFT JOIN FETCH u.profileImage
	  WHERE rs.id = :id
	  """)
	public Optional<ReadStatus> findReadStatusDetailsById(@Param("id") UUID id);

	@Query("""
	      SELECT rs
	      FROM ReadStatus rs
	      JOIN FETCH rs.channel c
	      JOIN FETCH rs.user u
	      LEFT JOIN FETCH u.profileImage
	      WHERE u.id = :userId
	  """)
	public List<ReadStatus> findAllByUserId(@Param("userId") UUID userId);

	public void deleteByChannelId(UUID channelId);

	@Query("""
	      SELECT DISTINCT new com.sprint.mission.discodeit.domain.dto.channel.ChannelDto(
	          c.id, c.type, c.name, c.description, null, null
	      )
	      FROM ReadStatus rs
	      JOIN rs.user u
	      JOIN rs.channel c
	      WHERE c.type = :publicType
	         OR (c.type != :publicType AND u.id = :userId)
	  """)
	List<ChannelDto> findAllChannelsForUser(@Param("userId") UUID userId, @Param("publicType") ChannelType publicType);

}
