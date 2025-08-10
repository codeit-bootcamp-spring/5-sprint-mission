package com.sprint.mission.discodeit.service.impl.dev;

import com.sprint.mission.discodeit.domain.entitydev.DevMessage;
import com.sprint.mission.discodeit.domain.entitydev.DevUser;
import com.sprint.mission.discodeit.repository.devrepository.DevMessageRepository;
import com.sprint.mission.discodeit.repository.devrepository.DevUserRepository;
import com.sprint.mission.discodeit.service.dev.DevMessageService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Profile({"test", "dev"})
public class BasicMessageService implements DevMessageService {

    private final DevMessageRepository messageRepository;
    private final DevUserRepository userRepository;

    public BasicMessageService(DevMessageRepository messageRepository,
                               DevUserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    protected void update(UUID id, Consumer<DevMessage> updater) {
        DevMessage entity = messageRepository.getOrThrow(id);
        updater.accept(entity);
        messageRepository.save(entity);
    }

    @Override
    public DevMessage send(UUID chatRoomId, UUID senderId, String content, Set<String> files, UUID replyTo) {
        Objects.requireNonNull(chatRoomId, "chatRoomId must not be null");
        Objects.requireNonNull(senderId, "senderId must not be null");
        userRepository.getOrThrow(senderId);

        if (replyTo != null) {
            DevMessage parent = messageRepository.getOrThrow(replyTo);
            if (!chatRoomId.equals(parent.getChatRoom()))
                throw new IllegalArgumentException("replyTo 메시지는 동일한 채팅방에 속해야 합니다.");
        }

        return messageRepository.save(new DevMessage(chatRoomId, senderId, content, files, replyTo));
    }

    @Override
    public void updateContent(UUID messageId, String content) {
        update(messageId, m -> m.setContent(content));
    }

    @Override
    public void updateFiles(UUID messageId, List<String> files) {
        Set<String> fileSet = (files == null) ? Set.of() : new LinkedHashSet<>(files);
        update(messageId, m -> m.setFiles(fileSet));
    }

    @Override
    public void printSenderAndContent(UUID messageId) {
        DevMessage m = messageRepository.getOrThrow(messageId);
        DevUser sender = userRepository.getOrThrow(m.getSender());
        System.out.println(sender.getGlobalName() + ": " + m.getContent());
    }
}
