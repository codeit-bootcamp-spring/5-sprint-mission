package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.main.Message;
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

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public Message create(UUID authorId, UUID channelId, String content) {
        userRepository.findById(authorId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel not found"));

        userRepository.findById(authorId).orElseThrow(NoSuchElementException::new);
        Message message = new Message(authorId,
                channelId,
                content
        );

        return messageRepository.save(message);
    }

    @Override
    public Message findById(UUID id) {
        Optional<Message> message = messageRepository.findById(id);
        return message.orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));
        message.update(content);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));
        messageRepository.deleteById(message.getId());
    }
}
