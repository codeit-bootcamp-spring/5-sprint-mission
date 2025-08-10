package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.domain.entity.ChatRoom;
import com.sprint.mission.discodeit.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Profile("prod")
public class JpaChatRoomService implements ChatRoomService {
    @Override
    public ChatRoom create(Set<UUID> participants) {
        return null;
    }

    @Override
    public Set<UUID> getMessages(UUID roomId) {
        return Set.of();
    }

    @Override
    public void addParticipant(UUID roomId, UUID userId) {

    }

    @Override
    public void removeParticipant(UUID roomId, UUID userId) {

    }
}
