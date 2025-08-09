package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.entitydev.DevChatRoom;
import com.sprint.mission.discodeit.repository.devrepository.DevChatRoomRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
@Profile("test")
public class JcfChatRoomRepository extends JcfBaseRepository<DevChatRoom> implements DevChatRoomRepository {

    @Override
    protected String getEntityTypeName() {
        return "ChatRoom";
    }

    @Override
    public boolean isParticipant(UUID chatRoomId, UUID userId) {
        if (chatRoomId == null || userId == null) return false;
        DevChatRoom chatRoom = data.get(chatRoomId);
        if (chatRoom == null || chatRoom.isDeleted() || chatRoom.isChannelChatRoom()) return false;
        return chatRoom.isParticipant(userId);
    }

    @Override
    public boolean existsByParticipants(Set<UUID> participants) {
        if (participants == null || participants.size() < 2 || participants.size() > 10 || participants.contains(null))
            return false;
        int participantsHashcode = DevChatRoom.computeParticipantsHashcode(participants);
        return data.values().stream()
                .filter(cr -> !cr.isDeleted())
                .filter(cr -> !cr.isChannelChatRoom())
                .filter(cr -> cr.participantsHashcode() == participantsHashcode)
                .anyMatch(cr -> cr.getParticipants().equals(participants));
    }
}
