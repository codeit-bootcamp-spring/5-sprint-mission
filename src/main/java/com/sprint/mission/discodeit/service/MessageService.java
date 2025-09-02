package com.sprint.mission.discodeit.service;

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
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.StorageRollbackManager;
import com.sprint.mission.discodeit.storage.StorageRollbackManager.StorageRollbackToken;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
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

    private final BinaryContentStorage binaryContentStorage;
    private final StorageRollbackManager storageRollbackManager;

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

    // 이런식으로 storage put/delete 처리를 여기서 하면 롤백 로직도 들어가고 관심사에 맞지 않는 것 같다.
    // storage에 put/delete 하는 것은 다른 서비스에서 처리하고 싶다.
    @Transactional
    public MessageDto create(MessageCreateRequest req, List<MultipartFile> attachments) {
        Channel channel = channelRepository.getOrThrow(req.channelId());
        User author = userRepository.getOrThrow(req.authorId());

        String content = req.content() != null ? req.content().strip() : null;

        Message m = messageRepository.save(new Message(content, channel, author));

        List<BinaryContent> binaryContents = new ArrayList<>();
        if (attachments != null && !attachments.isEmpty()) {
            StorageRollbackToken rollback = storageRollbackManager.start(binaryContentStorage);

            int orderIndex = 0;
            for (MultipartFile attachment : attachments) {
                if (attachment == null || attachment.isEmpty()) {
                    continue;
                }

                BinaryContent bc = binaryContentRepository.save(
                    new BinaryContent(
                        attachment.getOriginalFilename(),
                        attachment.getSize(),
                        attachment.getContentType()
                    )
                );

                try {
                    binaryContentStorage.put(bc.getId(), attachment.getBytes());
                    rollback.add(bc.getId());
                } catch (IOException e) {
                    throw new UncheckedIOException("첨부 파일 저장 실패: " + bc.getId(), e);
                }

                messageAttachmentRepository.save(new MessageAttachment(m, bc, orderIndex++));
                binaryContents.add(bc);
            }
        }

        return messageMapper.toDto(m, binaryContents);
    }

    // db에서 삭제하면 됐지, storage 삭제는 나중에 처리해도 되는 것
    @Transactional
    public void delete(UUID messageId) {
        List<BinaryContent> attachments =
            messageAttachmentRepository.findAttachmentsByMessageId(messageId);
        for (BinaryContent attachment : attachments) {
            binaryContentStorage.delete(attachment.getId());
        }
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
