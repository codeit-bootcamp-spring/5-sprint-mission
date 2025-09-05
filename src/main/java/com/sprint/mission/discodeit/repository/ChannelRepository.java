package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.enums.ChannelType;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

	@Query("""
	  SELECT c
	  FROM Channel c
	  where c.type = :publicType
	  """)
	List<Channel> findPublicChannels(@Param("publicType") ChannelType publicType);
}
