package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    MessageRepository messageRepository;
    UserService userService;
    ChannelService channelService;

    public BasicMessageService(MessageRepository messageRepository, UserService userService, ChannelService channelService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(Message message) {
        userService.searchById(message.getSenderId());
        channelService.searchById(message.getChannelId());
        return messageRepository.save(message);
    }

    @Override
    public Message updateContent(UUID id, String content) {
        Message message = searchById(id);
        message.updateContent(content);
        return messageRepository.save(message);
    }

    @Override
    public Message updateSenderId(UUID id, UUID senderId) {
        Message message = searchById(id);
        message.updateSenderId(senderId);
        return messageRepository.save(message);
    }

    @Override
    public Message updateChannelId(UUID id, UUID channelId) {
        Message message = searchById(id);
        message.updateSenderId(id);
        return messageRepository.save(message);
    }

    @Override
    public Message delete(UUID id) {
        return messageRepository.delete(id).orElseThrow(() -> new NoSuchElementException("해당하는 메세지를 찾을 수 없습니다."));
    }

    @Override
    public void deleteAll() {
        messageRepository.deleteAll();
    }

    @Override
    public Message searchById(UUID id) {
        return messageRepository.searchById(id).orElseThrow(() -> new NoSuchElementException("해당하는 메세지를 찾을 수 없습니다."));
    }

    @Override
    public List<Message> searchByContent(String content) {
        List<Message> messages = messageRepository.searchByContent(content);
        if (messages.isEmpty()) {
            throw new NoSuchElementException("해당하는 메세지를 찾을 수 없습니다.");
        }
        return messages;
    }

    @Override
    public List<Message> searchBySenderId(UUID id) {
        List<Message> messages = messageRepository.searchBySenderId(id);
        if (messages.isEmpty()) {
            throw new NoSuchElementException("해당하는 메세지를 찾을 수 없습니다.");
        }
        return messages;
    }

    @Override
    public List<Message> searchAll() {
        return messageRepository.searchAll();
    }
}
