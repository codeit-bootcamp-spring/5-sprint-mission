package com.codeit.mission.discodeit.repository;

import com.codeit.mission.discodeit.entity.Channel;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    
}