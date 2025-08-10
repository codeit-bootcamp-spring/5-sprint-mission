package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.AppStorageProperties;
import com.sprint.mission.discodeit.domain.entitydev.DevChatRoom;
import com.sprint.mission.discodeit.repository.devrepository.DevChatRoomRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
@Profile("dev")
public class FileChatRoomRepository extends FileBaseRepository<DevChatRoom> implements DevChatRoomRepository {
    public FileChatRoomRepository(AppStorageProperties storageProperties) {
        super(DevChatRoom.class, storageProperties);
    }

    @Override
    public boolean isParticipant(UUID chatRoomId, UUID userId) {
        if (chatRoomId == null || userId == null) return false;
        return findById(chatRoomId)
                .filter(cr -> !cr.isChannelChatRoom())
                .map(cr -> cr.isParticipant(userId))
                .orElse(false);
    }

    @Override
    public boolean existsByParticipants(Set<UUID> participants) {
        if (participants == null || participants.size() < 2 || participants.size() > 10 || participants.contains(null))
            return false;

        Set<UUID> target = Set.copyOf(participants);
        int targetHash = DevChatRoom.computeParticipantsHashcode(target);

        return findAll().stream()
                .filter(cr -> !cr.isChannelChatRoom())
                .filter(cr -> cr.getParticipants().size() == target.size())
                .filter(cr -> cr.getParticipantsHashcode() == targetHash)
                .anyMatch(cr -> cr.getParticipants().equals(target));
    }
}
