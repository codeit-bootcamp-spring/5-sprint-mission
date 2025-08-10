package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.domain.entity.ChatRoom;
import com.sprint.mission.discodeit.repository.ChatRoomRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Profile({"test", "dev"})
public class BasicChatRoomService implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    protected void update(UUID id, Consumer<ChatRoom> updater) {
        ChatRoom entity = chatRoomRepository.getOrThrow(id);
        updater.accept(entity);
        chatRoomRepository.save(entity);
    }

    @Override
    public ChatRoom create(Set<UUID> participants) {
        return chatRoomRepository.save(new ChatRoom(participants));
    }

    @Override
    public Set<UUID> getMessages(UUID chatRoomId) {
        return chatRoomRepository.getOrThrow(chatRoomId).getMessageIds();
    }

    @Override
    public void addParticipant(UUID chatRoomId, UUID userId) {
        userRepository.getOrThrow(userId);
        ChatRoom chatRoom = chatRoomRepository.getOrThrow(chatRoomId);
        if (chatRoom.isChannelChatRoom())
            throw new UnsupportedOperationException("채널 기반 ChatRoom은 GuildService에서 멤버를 수정해야 합니다.");
        update(chatRoomId, room -> room.addParticipant(userId));
    }

    @Override
    public void removeParticipant(UUID chatRoomId, UUID userId) {
        ChatRoom chatRoom = chatRoomRepository.getOrThrow(chatRoomId);
        if (chatRoom.isChannelChatRoom())
            throw new UnsupportedOperationException("채널 기반 ChatRoom은 GuildService에서 멤버를 수정해야 합니다.");
        update(chatRoomId, room -> room.removeParticipant(userId));
    }
}
