package com.sprint.mission.discodeit.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sprint.mission.discodeit.domain.entity.Message;

public interface MessageRepository extends JpaRepository<Message, UUID> {

	void deleteByChannelId(UUID channelId);

	Page<Message> findAllByChannelId(UUID channelId, Pageable pageable);

}
