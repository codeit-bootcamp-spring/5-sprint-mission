package com.sprint.mission.discodeit.service;

import static com.sprint.mission.discodeit.support.Utils.toBinaryContentFromMultipartFile;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageAttachment;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageAttachmentRepository;
import com.sprint.mission.discodeit.repository.MessageAttachmentRepository.MessageBinaryRow;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    private final BinaryContentMapper binaryContentMapper;
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    public List<MessageDto> findAllByChannelId(UUID channelId) {
        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        if (messages.isEmpty()) {
            return List.of();
        }

        List<UUID> messageIds = messages.stream().map(Message::getId).toList();

        List<MessageBinaryRow> rows =
            messageAttachmentRepository.findBinariesByMessageIds(messageIds);

        Map<UUID, List<BinaryContent>> messageIdToBinaries = rows.stream()
            .collect(Collectors.groupingBy(
                MessageBinaryRow::getMessageId,
                Collectors.mapping(MessageBinaryRow::getAttachment,
                    Collectors.toList())
            ));

        return messages.stream()
            .map(m -> messageMapper.toDto(
                m,
                messageIdToBinaries.getOrDefault(m.getId(), List.of())
            ))
            .toList();
    }

    @Transactional
    public MessageDto create(MessageCreateRequest req, List<MultipartFile> attachments) {
        Channel channel = channelRepository.getOrThrow(req.channelId());
        User author = userRepository.getOrThrow(req.authorId());

        String content = req.content() != null ? req.content().strip() : null;

        Message m = messageRepository.save(new Message(content, channel, author));

        List<BinaryContent> binaryContents = new ArrayList<>();
        int orderIndex = 0;
        for (MultipartFile attachment : attachments) {
            if (attachment != null && !attachment.isEmpty()) {
                BinaryContent bc = binaryContentRepository.save(
                    toBinaryContentFromMultipartFile(attachment));
                messageAttachmentRepository.save(
                    new MessageAttachment(m, bc, orderIndex++));
                binaryContents.add(bc);
            }
        }

        return messageMapper.toDto(m, binaryContents);
    }

    @Transactional
    public void delete(UUID messageId) {
        messageRepository.deleteById(messageId);
    }

    @Transactional
    public MessageDto update(UUID messageId, MessageUpdateRequest req) {
        Message m = messageRepository.getOrThrow(messageId);

        if (req.newContent() != null) {
            m.setContent(req.newContent().strip());
        }

        List<BinaryContent> attachments =
            messageAttachmentRepository.findAttachmentsByMessageId(messageId);

        return messageMapper.toDto(m, attachments);
    }
}
