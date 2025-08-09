package com.sprint.mission.discodeit.service.impl.dev;

import com.sprint.mission.discodeit.domain.entitydev.DevMessage;
import com.sprint.mission.discodeit.domain.entitydev.DevUser;
import com.sprint.mission.discodeit.repository.devrepository.DevMessageRepository;
import com.sprint.mission.discodeit.repository.devrepository.DevUserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Profile({"test", "dev"})
public class DevMessageService implements MessageService {

    private final DevMessageRepository messageRepository;
    private final DevUserRepository userRepository;

    public DevMessageService(DevMessageRepository messageRepository,
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
    public UUID send(UUID senderId, UUID receiverId, String content, Set<String> files, UUID replyTo) {
        return messageRepository.save(new DevMessage(senderId, receiverId, content, files, replyTo)).getId();
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
