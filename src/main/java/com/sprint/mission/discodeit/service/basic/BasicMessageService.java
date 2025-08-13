package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("messageService")
@RequiredArgsConstructor
@Validated
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(@Valid MessageCreateRequest messageCreateRequest,
                          @Valid List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        String content = messageCreateRequest.content();
        UUID authorId = messageCreateRequest.authorId();
        UUID channelId = messageCreateRequest.channelId();
        validateExist(authorId, channelId);

        List<UUID> attachmentIds = binaryContentCreateRequests.stream()
                .map(request -> {
                    BinaryContent binaryContent = new BinaryContent(request.fileName(), request.contentType(), request.bytes());
                    BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);
                    return createdBinaryContent.getId();
                })
                .toList();

        Message message = new Message(content, authorId, channelId, attachmentIds);

        return messageRepository.save(message);
    }

    @Override
    public Message findById(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("findById : 메세지를 찾을 수 없습니다"));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("findAllByChannelId : 채널을 찾을 수 없습니다");
        }
        return messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public Message update(@Valid MessageUpdateRequest messageUpdateRequest) {
        Message message = messageRepository.findById(messageUpdateRequest.messageId())
                .orElseThrow(() -> new NoSuchElementException("update : 메세지를 찾을 수 없습니다"));
        message.update(messageUpdateRequest.content());

        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("delete : 메세지를 찾을 수 없습니다"));

        for (UUID binaryContentId : message.getAttachmentIds()) {
            binaryContentRepository.deleteById(binaryContentId);
        }
        messageRepository.deleteById(message.getId());
    }

    private void validateExist(UUID authorId, UUID channelId) {
        if (!userRepository.existsById(authorId) || !channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("validateExist : 해당 유저 또는 채널을 찾을 수 없습니다.");
        }
    }
}
