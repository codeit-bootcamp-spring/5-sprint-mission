package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageDto.DetailResponse create(MessageDto.CreateRequest request) {
        User author = userRepository.findById(request.getAuthorId()).orElse(null);
        Channel channel = channelRepository.findById(request.getChannelId()).orElse(null);

        if (author == null || channel == null) {
            return null;
        }

        Message message = messageRepository.save(new Message(request.getText(), request.getChannelId(), request.getAuthorId()));

        return MessageDto.DetailResponse.builder()
            .id(message.getId())
            .channelId(message.getChannelId())
            .authorId(message.getAuthorId())
            .channelName(channel.getName())
            .authorName(author.getName())
            .text(message.getText())
            .createdAt(message.getCreatedAt())
            .updatedAt(message.getUpdatedAt())
            .build();
    }

    @Override
    public MessageDto.DetailResponse update(MessageDto.UpdateRequest request) {

        Message message = messageRepository.findById(request.getId()).orElse(null);
        if (message == null) {
            return null;
        }

        User author = userRepository.findById(message.getAuthorId()).orElse(null);
        Channel channel = channelRepository.findById(message.getChannelId()).orElse(null);
        if (author == null || channel == null) {
            return null;
        }

        message.update(request.getText());
        return MessageDto.DetailResponse.builder()
            .id(message.getId())
            .channelId(message.getChannelId())
            .authorId(message.getAuthorId())
            .channelName(channel.getName())
            .authorName(author.getName())
            .text(message.getText())
            .createdAt(message.getCreatedAt())
            .updatedAt(message.getUpdatedAt())
            .build();
    }

    @Override
    public MessageDto.DetailResponse findById(UUID id) {
        Message message = messageRepository.findById(id).orElse(null);
        if (message == null) {
            return null;
        }

        User author = userRepository.findById(message.getAuthorId()).orElse(null);
        Channel channel = channelRepository.findById(message.getChannelId()).orElse(null);

        return MessageDto.DetailResponse.builder()
            .id(message.getId())
            .channelId(message.getChannelId())
            .authorId(message.getAuthorId())
            .channelName(channel.getName())
            .authorName(author.getName())
            .text(message.getText())
            .createdAt(message.getCreatedAt())
            .updatedAt(message.getUpdatedAt())
            .build();
    }

    @Override
    public List<MessageDto.DetailResponse> findAllByChannelId(UUID channelId) {
        List<Message> messages = messageRepository.findAllByChannelId(channelId);

        Channel channel = channelRepository.findById(channelId).orElse(null);
        List<User> users = messages.stream()
            .map(Message::getAuthorId)
            .distinct()
            .map(id -> userRepository.findById(id).orElse(null))
            .toList();

        return messages.stream().map(m ->
            MessageDto.DetailResponse.builder()
                .id(m.getId())
                .channelId(m.getChannelId())
                .authorId(m.getAuthorId())
                .channelName(channel.getName())
                .authorName(users.stream()
                    .filter(u -> u.getId().equals(m.getAuthorId()))
                    .findFirst().orElse(null).getName())
                .text(m.getText())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        // 관련된 도메인도 같이 삭제합니다. 첨부파일(BinaryContent)
        Message message = messageRepository.findById(id).orElse(null);

        if (message != null) {
            messageRepository.delete(id);
            if (!message.getAttachmentIds().isEmpty()) {
                List<BinaryContent> contents = binaryContentRepository.findAllByIdIn(message.getAttachmentIds());

                contents.forEach(c -> {
                    binaryContentRepository.delete(c.getId());
                });
            }
        }
    }

    @Override
    public void deleteAll() {
        messageRepository.deleteAll();
    }
}
