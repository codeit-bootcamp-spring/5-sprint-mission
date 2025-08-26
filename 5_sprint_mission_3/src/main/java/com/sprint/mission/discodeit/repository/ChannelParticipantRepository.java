package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ChannelParticipant;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChannelParticipantRepository {
    ChannelParticipant save(ChannelParticipant channelParticipant);
    Optional<ChannelParticipant> findByUserId(UUID id);
    List<ChannelParticipant> findAllByChannelId(UUID channelId);
    List<ChannelParticipant> findAllByUserId(UUID userId);
    boolean existsByChannelIdAndUserId(UUID channelId, UUID userId);
    void deleteById(UUID id);
    void deleteByChannelIdAndUserId(UUID channelId, UUID userId);
}
