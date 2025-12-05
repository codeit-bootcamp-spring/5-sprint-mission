package com.sprint.mission.discodeit.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sprint.mission.discodeit.entity.Message;

public interface MessageRepository extends JpaRepository<Message, UUID>, MessageQueryRepository {
}
