// package com.sprint.mission.discodeit.service.message;
//
// import static com.sprint.mission.discodeit.support.StringUtil.nullOrStrip;
//
// import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
// import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
// import com.sprint.mission.discodeit.dto.message.MessageDto;
// import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
// import com.sprint.mission.discodeit.dto.user.UserDto;
// import com.sprint.mission.discodeit.entity.BinaryContent;
// import com.sprint.mission.discodeit.entity.Channel;
// import com.sprint.mission.discodeit.entity.Message;
// import com.sprint.mission.discodeit.entity.User;
// import com.sprint.mission.discodeit.entity.UserStatus;
// import com.sprint.mission.discodeit.exception.NotFoundException;
// import com.sprint.mission.discodeit.repository.BinaryContentRepository;
// import com.sprint.mission.discodeit.repository.ChannelRepository;
// import com.sprint.mission.discodeit.repository.MessageRepository;
// import com.sprint.mission.discodeit.repository.UserRepository;
// import com.sprint.mission.discodeit.repository.UserStatusRepository;
// import com.sprint.mission.discodeit.support.FileNames;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.LinkedHashSet;
// import java.util.List;
// import java.util.Set;
// import java.util.UUID;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.multipart.MultipartFile;
//
// @Slf4j
// @Service
// @RequiredArgsConstructor
// @Transactional(readOnly = true)
// public class MessageService {
//
//   private final MessageRepository messageRepository;
//   private final ChannelRepository channelRepository;
//   private final UserRepository userRepository;
//   private final UserStatusRepository userStatusRepository;
//   private final BinaryContentRepository binaryContentRepository;
//
//   private MessageDto toResponse(Message m) {
//     User user = userRepository.find(m.getAuthorId()).orElse(null);
//     UserStatusType userStatusType = userStatusRepository.findByUserId(user.getId())
//         .map(UserStatus::getType)
//         .orElse(UserStatusType.OFFLINE);
//     BinaryContent bc = binaryContentRepository.find(user.getProfile()).orElse(null);
//     BinaryContentDto profile = bc != null ? BinaryContentDto.from(bc) : null;
//     UserDto author = UserDto.from(user, userStatusType, profile);
//     List<BinaryContent> bcs = binaryContentRepository.findAllByIdIn(m.getAttachmentIds());
//     List<BinaryContentDto> attachments = new ArrayList<>();
//     for (BinaryContent b : bcs) {
//       attachments.add(BinaryContentDto.from(b));
//     }
//     return MessageDto.from(m, author, attachments);
//   }
//
//
//   public List<MessageDto> findAllByChannelId(UUID channelId) {
//     Channel c = channelRepository.getOrThrow(channelId);
//
//     return messageRepository.findAllByIdIn(c.getMessageIds()).stream()
//         .map(this::toResponse)
//         .toList();
//   }
//
//   @Transactional
//   public MessageDto create(
//       MessageCreateRequest req,
//       List<MultipartFile> attachments
//   ) throws IOException {
//     Channel c = channelRepository.getOrThrow(req.channelId());
//     userRepository.getOrThrow(req.authorId());
//
//     Set<UUID> attachmentIds = new LinkedHashSet<>();
//     if (attachments != null) {
//       for (MultipartFile attachment : attachments) {
//         if (attachment == null || attachment.isEmpty()) {
//           continue;
//         }
//         String ct = FileNames.normalizeContentType(attachment.getContentType());
//         String original = attachment.getOriginalFileName();
//         String fileName = FileNames.buildStoredName(original, ct);
//
//         BinaryContent saved = binaryContentRepository.save(
//             new BinaryContent(fileName, ct, attachment.getBytes())
//         );
//
//         attachmentIds.add(saved.getId());
//       }
//     }
//
//     Message m = messageRepository.save(
//         new Message(
//             req.channelId(),
//             req.authorId(),
//             nullOrStrip(req.content()),
//             attachmentIds
//         )
//     );
//
//     c.addMessageId(m.getId());
//     channelRepository.save(c);
//
//     return toResponse(m);
//   }
//
//   @Transactional
//   public void delete(UUID messageId) {
//     if (!messageRepository.delete(messageId)) {
//       throw new NotFoundException("Message with id %s not found".formatted(messageId));
//     }
//   }
//
//   @Transactional
//   public MessageDto update(UUID messageId, MessageUpdateRequest req) {
//     Message m = messageRepository.getOrThrow(messageId);
//     return toResponse(
//         messageRepository.save(
//             m.update(nullOrStrip(req.newContent()))
//         )
//     );
//   }
// }
