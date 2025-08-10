package com.sprint.mission.discodeit.serviceprod;

import com.sprint.mission.discodeit.domain.entityprod.ProdChatRoom;
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
public class JpaChatRoomService {
    public ProdChatRoom create(Set<UUID> participants) {
        return null;
    }

    public Set<UUID> getMessages(UUID roomId) {
        return Set.of();
    }

    public void addParticipant(UUID roomId, UUID userId) {

    }

    public void removeParticipant(UUID roomId, UUID userId) {

    }
}
