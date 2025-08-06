package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageRepository messageRepository;

    public JCFMessageService(UserService userService, ChannelService channelService, JCFMessageRepository messageRepository) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageRepository = messageRepository;
    }

    @Override
    public Message createMessage(UUID senderId, UUID channelId, String name, String title, String content) {
        if (userService.readUser(senderId) == null | channelService.readChannel(channelId) == null) {
            return null;
        }
        Message message = new Message(senderId, channelId, name, title, content);
        messageRepository.save(message);
        return message;
    }

    @Override
    public Optional<Message> readMessage(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> readAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    public Message updateMessage(Message message) {
        try {
            messageRepository.update(message.getId(), message);
            System.out.println("수정 완료: " + message);
        } catch (NoSuchElementException e) {
            System.out.println("Message not found");
        }
        return message;
    }

    @Override
    public void deleteMessage(UUID id) {
        if (messageRepository.existsById(id)) {
            System.out.println("삭제 성공");
            messageRepository.deleteById(id);
        } else {
            System.out.println("Message not found");
        }
    }
}
