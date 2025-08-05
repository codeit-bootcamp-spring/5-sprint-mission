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
    public Message create(Message message) {

        if (message == null) {
            return null;
        }

        if (message.getText() == null || message.getChannelId() == null || message.getAuthorId() == null) {
            return null;
        }

        return messageRepository.save(message);
    }

    @Override
    public Message create(String text, UUID channelId, UUID userId) {

        if (text == null || channelId == null || userId == null) {
            return null;
        }

        return messageRepository.save(new Message(text, channelId, userId));
    }

    @Override
    public List<Message> getAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message get(UUID id) {
        return messageRepository.findById(id).orElse(null);
    }

    @Override
    public Message update(UUID id, String text) {
        Message message = messageRepository.findById(id).orElse(null);

        if (message == null) {
            return null;
        }

        message.update(text);
        return messageRepository.save(message);
    }
    // TODO 불필요 정리 예정

    @Override
    public MessageDto.DetailResponse create(MessageDto.CreateRequest request) {

        Message message = messageRepository.save(new Message(request.getText(), request.getChannelId(), request.getAuthorId()));
        User author = userRepository.findById(request.getAuthorId()).orElse(null);
        Channel channel = channelRepository.findById(request.getChannelId()).orElse(null);

        return MessageDto.DetailResponse.builder()
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

        message.update(request.getText());

        User author = userRepository.findById(message.getAuthorId()).orElse(null);
        Channel channel = channelRepository.findById(message.getChannelId()).orElse(null);

        return MessageDto.DetailResponse.builder()
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

        // TODO  추후 유저 작업..

        return messages.stream().map(m ->
            MessageDto.DetailResponse.builder()
            .channelId(m.getChannelId())
            .authorId(m.getAuthorId())
            .channelName(channel.getName())
//                .authorName() // TODO 추후 작업...
            .text(m.getText())
            .createdAt(m.getCreatedAt())
            .updatedAt(m.getUpdatedAt())
            .build()).collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        // 관련된 도메인도 같이 삭제합니다. 첨부파일(BinaryContent)
        Message message = messageRepository.findById(id).orElse(null);

        if (message != null) {
            messageRepository.delete(id);
            if(!message.getAttachmentIds().isEmpty()){
                List<BinaryContent> contents = binaryContentRepository.findAllByIdIn(message.getAttachmentIds());

                contents.forEach(c -> {binaryContentRepository.delete(c.getId());});
            }
        }
    }

    @Override
    public void deleteAll() {
        messageRepository.deleteAll();
    }
}
