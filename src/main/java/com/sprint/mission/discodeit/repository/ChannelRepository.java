package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Channel 전용 레포지토리
 * - 기본 CRUD는 JpaRepository가 제공
 */
public interface ChannelRepository extends JpaRepository<Channel, UUID> {
}
