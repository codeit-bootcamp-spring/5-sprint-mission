package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sprint.mission.discodeit.domain.entity.Message;

public interface MessageRepository extends JpaRepository<Message, UUID> {

	void deleteByChannelId(UUID channelId);

	List<Message> findAllByChannelId(UUID channelId);

}
