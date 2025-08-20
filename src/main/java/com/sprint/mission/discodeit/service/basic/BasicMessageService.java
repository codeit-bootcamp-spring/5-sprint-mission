package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponse createMessage(CreateMessageRequest request) {
        List<UUID> attachmentIds = Optional.ofNullable(request.attachments())
                .orElse(List.of())
                .stream()
                .map(file -> binaryContentRepository.save(
                        new BinaryContent(file.fileName(), file.fileType(), file.data(), file.fileSize())
                ).getId())
                .toList();

        Optional<User> optionalUser = userRepository.findById(request.senderId());
        if (optionalUser.isEmpty()) return null;

        Optional<Channel> optionalChannel = channelRepository.findById(request.channelId());
        if (optionalChannel.isEmpty()) return null;

        Message message = new Message(optionalUser.get(), optionalChannel.get(), request.content(), attachmentIds);
        return toResponse(message);
    }

    @Override
    public List<MessageResponse> getAllByChannelId(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<MessageResponse> getAllByUserId(UUID userId) {
        return messageRepository.findAll().stream()
                .filter(message -> message.getUser().getId().equals(userId))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<MessageResponse> getAllByMessage(String content) {
        return messageRepository.findAll().stream()
                .filter(message -> message.getContent().contains(content))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public MessageResponse update(UpdateMessageRequest request) {
        Optional<Message> optionalMessage = messageRepository.findById(request.messageId());
        if (optionalMessage.isEmpty()) return null;

        Message message = optionalMessage.get();
        if (request.content().isEmpty()) {
            remove(request.messageId());
            return null;
        }

        message.updateMessage(request.content());
        messageRepository.save(message);

        return toResponse(message);
    }

    @Override
    public boolean remove(UUID messageId) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isEmpty()) return false;

        Message message = optionalMessage.get();
        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.delete(attachmentId);
        }

        return messageRepository.delete(messageId);
    }

    private MessageResponse toResponse(Message message) {
        return new MessageResponse(message.getId(), message.getChannel().getId(), message.getUser().getId(), message.getContent(), message.getAttachmentIds(), message.getCreateAt());
    }
}
