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
    public Message createMsg(String content, String channelId, String authorId) {
        // 채널과 사용자 ID는 문자열이므로 그대로 사용
        if (!channelRepository.existsById(UUID.fromString(channelId))) {
            throw new NoSuchElementException("Channel not found with id " + channelId);
        }

        if (!userRepository.existsById(UUID.fromString(authorId))) {
            throw new NoSuchElementException("Author not found with id " + authorId);
        }

        Message message = new Message(content, channelId, authorId);
        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId).orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID messageId, String content, String channelId, String authorId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() ->
                new NoSuchElementException("Message with id " + messageId + " not found"));
        message.update(content,channelId,authorId);
        return messageRepository.save(message);
    }

    @Override
    public Message delete(UUID messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() ->
                new NoSuchElementException("Message with id " + messageId + " not found"));
        return messageRepository.delete(messageId);
    }
}
