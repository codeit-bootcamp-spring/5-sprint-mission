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
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageAttachmentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;

    private final BinaryContentMapper binaryContentMapper;
    private final UserMapper userMapper;
    private final MessageMapper messageMapper;

    public List<MessageDto> findAllByChannelId(UUID channelId) {
        Channel c = channelRepository.getOrThrow(channelId);
        return List.of();
    }

    @Transactional
    public MessageDto create(MessageCreateRequest req, List<MultipartFile> attachments) {
        Channel channel = channelRepository.getOrThrow(req.channelId());
        User author = userRepository.getOrThrow(req.authorId());
        UserStatus authorStatus = userStatusRepository.getOrCreateByUser(author);

        String content = req.content() != null ? req.content().strip() : null;

        Message m = messageRepository.save(new Message(content, channel, author));

        List<BinaryContent> binaryContents = new ArrayList<>();
        int orderIndex = 0;
        for (MultipartFile attachment : attachments) {
            if (attachment != null && !attachment.isEmpty()) {
                BinaryContent bc = binaryContentRepository.save(
                    toBinaryContentFromMultipartFile(attachment));
                messageAttachmentRepository.save(
                    new MessageAttachment(m.getId(), bc.getId(), orderIndex++));
                binaryContents.add(bc);
            }

        }

        return messageMapper.toDto(m, userMapper.toDto(author, authorStatus), binaryContents);
    }

    @Transactional
    public void delete(UUID messageId) {
        messageRepository.deleteById(messageId);
    }

    @Transactional
    public MessageDto update(UUID messageId, MessageUpdateRequest req) {
        Message m = messageRepository.getOrThrow(messageId);
        if (req.newContent() != null) {
            m.setContent(req.newContent());
        }
        return null;
    }
}
