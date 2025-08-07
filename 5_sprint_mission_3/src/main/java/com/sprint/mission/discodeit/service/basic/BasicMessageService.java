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
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("basicMessageService")
@Primary
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateRequest request) {
        // 유효성 검사
        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("채널이 존재하지 않습니다.");
        }
        if (!userRepository.existsById(request.authorId())) {
            throw new NoSuchElementException("사용자가 존재하지 않습니다.");
        }

        List<UUID> attachmentIds = new ArrayList<>();
        if (request.attachments() != null) {
            for (BinaryContentCreateRequest attachment : request.attachments()) {
                BinaryContent saved = binaryContentRepository.save(new BinaryContent(
                        attachment.filename(),
                        attachment.contentType(),
                        attachment.size(),
                        attachment.data()
                ));
                attachmentIds.add(saved.getId());
            }
        }

        Message message = new Message(
                request.content(),
                request.channelId(),
                request.authorId(),
                attachmentIds
        );
        return messageRepository.save(message);
    }


    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("채널 Id가 존재하지 않습니다.");
        }
        return messageRepository.findAllByChannelId(channelId);
    }

    @Override
    public Message update(MessageUpdateRequest request) {
        Message message = messageRepository.findById(request.messageId())
                .orElseThrow(() -> new NoSuchElementException("메시지 Id가 존재하지 않습니다."));
        message.update(request.newContent());
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("메시지 Id가 존재하지 않습니다."));

        List<UUID> attachmentIds = message.getAttachmentIds();

        if(attachmentIds != null) {
            for (UUID attachmentId : attachmentIds) {
                binaryContentRepository.deleteById((attachmentId));
            }
        }
        messageRepository.deleteById(messageId);
    }
}
