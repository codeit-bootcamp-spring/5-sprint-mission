package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public Message create(Message message) {
        Optional<Channel> existingChannel = channelRepository.findById(message.getChannelId());
        Optional<User> existingUser = userRepository.findById(message.getUserId());

        if (existingChannel.isEmpty()) {
            throw new RuntimeException("[Basic]Message creation failed: Channel with ID" + message.getChannelId());
        }
        if (existingUser.isEmpty()) {
            throw new RuntimeException("[Basic]Message creation failed: User with ID" + message.getUserId());
        }

        Channel channel = existingChannel.get();
        if (!channel.isMember(message.getUserId())) {
            throw new RuntimeException("[Basic]Message creation failed: Channel with ID" + message.getUserId());
        }

        messageRepository.save(message);
        System.out.println("[Basic]Message created: " + message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        System.out.println("[Basic]Message findById: " + id);
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> findAll() {
        System.out.println("[Basic]Message findAll");
        return messageRepository.findAll();
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        System.out.println("[Basic]Message findByChannelId: " + channelId);
        if (channelRepository.findById(channelId).isEmpty()) {
            System.out.println("[Basic]Message findByChannelId: channel with ID" + channelId);
            return new ArrayList<>();
        }
        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public void delete(UUID id) {
        Optional<Message> message = messageRepository.findById(id);
        if (message.isEmpty()) {
            throw new RuntimeException("[Basic]Message deletion failed: Message with ID" + id);
        }

        try {
            messageRepository.delete(id);
            System.out.println("[Basic]Message deleted: " + id);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
