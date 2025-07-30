package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final MessageRepository messageRepository;

    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(
            MessageRepository messageRepository,
            UserService userService,
            ChannelService channelService
    ) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message addMessage(String messageContent, UUID channelId, UUID userId) {
        User userById = userService.getUserById(userId);
        Channel channelById = channelService.getChannelById(channelId);
        Message message = new Message(messageContent, channelById, userById);
        return messageRepository.save(message).orElseThrow();
    }

    @Override
    public Message getMessageById(UUID messageId) {
        return messageRepository.findById(messageId).orElseThrow();
    }

    @Override
    public List<Message> getAllMessage() {
        return messageRepository.findAll();
    }

    @Override
    public Message updateMessage(UUID messageId, String messageContent) {
        Message message = messageRepository.findById(messageId).orElseThrow();
        message.updateContent(messageContent);
        return messageRepository.save(message).orElseThrow();
    }

    @Override
    public void deleteMessage(UUID messageId) {
        messageRepository.findById(messageId).ifPresent(messageRepository::delete);
    }

    @Override
    public void deleteAllMessage() {
        messageRepository.deleteAll();
    }
}
