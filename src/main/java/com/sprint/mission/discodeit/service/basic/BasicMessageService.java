package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.respository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    public Message create(MessageDto.Create dto) {
        Message message = new Message(dto.user(), dto.channel(), dto.content());
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
