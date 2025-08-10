package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.entity.ChatRoom;
import com.sprint.mission.discodeit.repository.ChatRoomRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
@Profile("test")
public class JcfChatRoomRepository extends JcfBaseRepository<ChatRoom> implements ChatRoomRepository {

    @Override
    protected String getEntityTypeName() {
        return "ChatRoom";
    }

    @Override
    public boolean isParticipant(UUID chatRoomId, UUID userId) {
        if (chatRoomId == null || userId == null) return false;
        ChatRoom chatRoom = data.get(chatRoomId);
        if (chatRoom == null || chatRoom.isDeleted() || chatRoom.isChannelChatRoom()) return false;
        return chatRoom.isParticipant(userId);
    }

    @Override
    public boolean existsByParticipants(Set<UUID> participants) {
        if (participants == null || participants.size() < 2 || participants.size() > 10 || participants.contains(null))
            return false;

        Set<UUID> target = Set.copyOf(participants);
        int targetHash = ChatRoom.computeParticipantsHashcode(target);

        return findAll().stream()
                .filter(cr -> !cr.isChannelChatRoom())
                .filter(cr -> cr.getParticipantIds().size() == target.size())
                .filter(cr -> cr.getParticipantsHashcode() == targetHash)
                .anyMatch(cr -> cr.getParticipantIds().equals(target));
    }
}
