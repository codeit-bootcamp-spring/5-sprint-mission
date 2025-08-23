package com.sprint.mission.discodeit.service.message;

import static com.sprint.mission.discodeit.support.StringUtil.nullOrStrip;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.support.FileNames;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;


  public List<MessageResponse> findAllByChannelId(UUID channelId) {
    Channel c = channelRepository.getOrThrow(channelId);

    return messageRepository.findAllById(c.getMessageIds()).stream()
        .map(MessageResponse::from)
        .toList();
  }

  @Transactional
  public MessageResponse create(
      MessageCreateRequest req,
      List<MultipartFile> attachments
  ) throws IOException {
    Channel c = channelRepository.getOrThrow(req.channelId());
    userRepository.getOrThrow(req.authorId());

    Set<UUID> attachmentIds = new LinkedHashSet<>();
    if (attachments != null && !attachments.isEmpty()) {
      for (MultipartFile attachment : attachments) {
        String ct = FileNames.normalizeContentType(attachment.getContentType());
        String original = attachment.getOriginalFilename();
        String fileName = FileNames.buildStoredName(original, ct);

        BinaryContent saved = binaryContentRepository.save(
            new BinaryContent(fileName, ct, attachment.getBytes())
        );

        attachmentIds.add(saved.getId());
      }
    }

    Message m = messageRepository.save(
        new Message(
            req.channelId(),
            req.authorId(),
            req.content(),
            attachmentIds
        )
    );

    c.addMessageId(m.getId());
    channelRepository.save(c);

    return MessageResponse.from(m);
  }

  @Transactional
  public void delete(UUID messageId) {
    messageRepository.softDeleteById(messageId);
  }

  @Transactional
  public MessageResponse update(
      UUID messageId,
      MessageUpdateRequest req
  ) {

    Message m = messageRepository.getOrThrow(messageId);

    return MessageResponse.from(
        messageRepository.save(
            m.update(
                nullOrStrip(req.content())
            )
        )
    );
  }
}
