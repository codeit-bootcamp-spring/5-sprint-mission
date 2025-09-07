package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    @Transactional
    public Message create(MessageCreateRequest messageCreateRequest,
                          List<BinaryContentCreateRequest> binaryContentCreateRequests) {

        User author = userRepository.findById(messageCreateRequest.authorId())
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));
        Channel channel = channelRepository.findById(messageCreateRequest.channelId())
                .orElseThrow(() -> new EntityNotFoundException("Channel not found"));

        Message message = new Message(messageCreateRequest.content(), channel, author);

        binaryContentCreateRequests.forEach(request -> {
            BinaryContent attachment = new BinaryContent(
                    request.fileName(),
                    (long) request.bytes().length,
                    request.contentType()
            );
            message.addAttachment(attachment); // 엔티티에 추가한 편의 메서드 사용
        });

        return messageRepository.save(message);
    }

    @Override
    @Transactional(readOnly = true)
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found: " + messageId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannel(channelId);
    }

    @Override
    @Transactional
    public Message update(UUID messageId, MessageUpdateRequest request) {
        Message message = find(messageId);

        message.update(request.newContent());
        return message;
    }

    @Override
    @Transactional
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new EntityNotFoundException("Message not found: " + messageId);
        }
        messageRepository.deleteById(messageId);
    }
}
