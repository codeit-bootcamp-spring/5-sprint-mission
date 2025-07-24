package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.respository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.*;

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;

    // 저장 방식(JCF 또는 File)에 따라 적절한 구현체를 주입받아 사용
    public BasicMessageService(MessageRepository messageRepository){
        this.messageRepository = messageRepository;
    }

    @Override
    public Message create(User user, Channel channel, String content) {
        Message message = new Message(user, channel, content);
        return messageRepository.save(message);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> findByStr(String str) {
        List<Message> result = new ArrayList<>();
        for (Message message : messageRepository.findAll()) {
            if (message.getContent().contains(str)) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public Message update(UUID id, String newMessage) {
        Message message = messageRepository.findById(id);
        if (message == null) {
            throw new NoSuchElementException("해당 ID의 메시지가 존재하지 않습니다.");
        }
        message.updateContent(newMessage);
        messageRepository.save(message);
        return message;
    }

    @Override
    public boolean deleteById(UUID id) {
        return messageRepository.deleteById(id);
    }
}
