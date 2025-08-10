package com.sprint.mission.discodeit.service.impl.dev;

import com.sprint.mission.discodeit.domain.entitydev.DevChatRoom;
import com.sprint.mission.discodeit.repository.devrepository.DevChannelRepository;
import com.sprint.mission.discodeit.repository.devrepository.DevChatRoomRepository;
import com.sprint.mission.discodeit.repository.devrepository.DevGuildRepository;
import com.sprint.mission.discodeit.repository.devrepository.DevMessageRepository;
import com.sprint.mission.discodeit.repository.devrepository.DevUserRepository;
import com.sprint.mission.discodeit.service.dev.DevChatRoomService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Profile({"test", "dev"})
public class BasicChatRoomService implements DevChatRoomService {

    private final DevChatRoomRepository chatRoomRepository;
    private final DevUserRepository userRepository;

    public BasicChatRoomService(DevChatRoomRepository chatRoomRepository,
                                DevUserRepository userRepository,
                                DevGuildRepository guildRepository,
                                DevChannelRepository channelRepository,
                                DevMessageRepository messageRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
    }

    protected void update(UUID id, Consumer<DevChatRoom> updater) {
        DevChatRoom entity = chatRoomRepository.getOrThrow(id);
        updater.accept(entity);
        chatRoomRepository.save(entity);
    }

    @Override
    public DevChatRoom create(Set<UUID> participants) {
        return chatRoomRepository.save(new DevChatRoom(participants));
    }

    @Override
    public Set<UUID> getMessages(UUID chatRoomId) {
        return chatRoomRepository.getOrThrow(chatRoomId).getMessages();
    }

    @Override
    public void addParticipant(UUID chatRoomId, UUID userId) {
        userRepository.getOrThrow(userId);
        DevChatRoom chatRoom = chatRoomRepository.getOrThrow(chatRoomId);
        if (chatRoom.isChannelChatRoom())
            throw new UnsupportedOperationException("채널 기반 ChatRoom은 GuildService에서 멤버를 수정해야 합니다.");
        update(chatRoomId, room -> room.addParticipant(userId));
    }

    @Override
    public void removeParticipant(UUID chatRoomId, UUID userId) {
        DevChatRoom chatRoom = chatRoomRepository.getOrThrow(chatRoomId);
        if (chatRoom.isChannelChatRoom())
            throw new UnsupportedOperationException("채널 기반 ChatRoom은 GuildService에서 멤버를 수정해야 합니다.");
        update(chatRoomId, room -> room.removeParticipant(userId));
    }
}
