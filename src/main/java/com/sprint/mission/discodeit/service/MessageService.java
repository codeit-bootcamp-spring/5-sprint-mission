package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Profile({"test", "dev"})
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    protected void update(UUID id, Consumer<Message> updater) {
        Message entity = messageRepository.getOrThrow(id);
        updater.accept(entity);
        messageRepository.save(entity);
    }

    // public Message send(UUID chatRoomId, UUID senderId, String content, Set<UUID> attachmentIds, UUID replyTo) {
    //     Objects.requireNonNull(chatRoomId, "chatRoomId must not be null");
    //     Objects.requireNonNull(senderId, "senderId must not be null");
    //     userRepository.getOrThrow(senderId);
    //
    //     if (replyTo != null) {
    //         Message parent = messageRepository.getOrThrow(replyTo);
    //         if (!chatRoomId.equals(parent.getChatRoomId()))
    //             throw new IllegalArgumentException("replyTo 메시지는 동일한 채팅방에 속해야 합니다.");
    //     }
    //
    //     return messageRepository.save(new Message(chatRoomId, senderId, content, attachmentIds, replyTo));
    // }

    public void updateContent(UUID messageId, String content) {
        update(messageId, m -> m.setContent(content));
    }

    public void updateAttachmentIds(UUID messageId, List<UUID> attachmentIds) {
        Set<UUID> attachmentSet = (attachmentIds == null) ? Set.of() : new LinkedHashSet<>(attachmentIds);
        update(messageId, m -> m.setAttachmentIds(attachmentSet));
    }

    public void printSenderAndContent(UUID messageId) {
        Message m = messageRepository.getOrThrow(messageId);
        User sender = userRepository.getOrThrow(m.getAuthorId());
        System.out.println(sender.getGlobalName() + ": " + m.getContent());
    }
}
