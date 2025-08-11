package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;


@Service("basicMessageService")
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel not found with id " + channelId);
        }
        if (!userRepository.existsById(authorId)) {
            throw new NoSuchElementException("Author not found with id " + authorId);
        }

        Message message = new Message(content, channelId, authorId);
        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("메세지를 찾을 수 없습니다"));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID messageId, String newContent) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("메세지를 찾을 수 없습니다"));
        message.update(newContent);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new NoSuchElementException("메세지를 찾을 수 없습니다");
        }
        messageRepository.deleteById(messageId);
    }
}
