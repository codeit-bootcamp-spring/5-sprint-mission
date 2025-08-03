package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicMessageService(MessageRepository messageRepository, ChannelRepository channelRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        if (content == null || content.isBlank() || channelId == null || authorId == null) {
            throw new IllegalArgumentException("message content or channelId or authorId is null or blank");
        }
        if (!channelRepository.existById(channelId)) {
            throw new NoSuchElementException("channel not found");
        }
        if (!userRepository.existsById(authorId)) {
            throw new NoSuchElementException("author not found");
        }

        Message message = new Message(content, channelId, authorId);
        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId).orElseThrow(() -> new NoSuchElementException("message not found"));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID messageId, String newContent) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new NoSuchElementException("message not found"));
        message.update(newContent);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existById(messageId)) {
            throw new NoSuchElementException("message not found");
        }
        messageRepository.delete(messageId);
    }
}
