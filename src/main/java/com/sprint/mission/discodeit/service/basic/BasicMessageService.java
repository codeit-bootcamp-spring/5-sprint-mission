package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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

@Service("BasicMessageService")
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Override
    public UUID create(MessageCreateRequest req) {
        // 존재 검증 등은 생략
        Message m = new Message(req.getUserId(), req.getChannelId(), req.getContent());
        messageRepository.save(m);
        return m.getId();
    }

    @Override
    public Optional<MessageResponse> find(UUID id) {
        return messageRepository.findById(id).map(MessageResponse::new);
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId)
                .stream()
                .map(MessageResponse::new)
                .toList();
    }

    @Override
    public boolean update(MessageUpdateRequest req) {
        return messageRepository.findById(req.getMessageId())
                .map(m -> {
                    m.updateContent(req.getNewContent());
                    messageRepository.save(m);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean delete(UUID id) {
        if (!messageRepository.existsById(id)) return false;
        messageRepository.deleteById(id);
        return true;
    }
}

