package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageDto.CreateCommand;
import com.sprint.mission.discodeit.dto.MessageDto.UpdateCommand;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public MessageDto.Detail create(CreateCommand create) {

    User author = userRepository.findById(create.getAuthorId())
        .orElseThrow(() -> new RuntimeException("User not found"));
    Channel channel = channelRepository.findById(create.getChannelId())
        .orElseThrow(() -> new RuntimeException("Channel not found"));

    List<BinaryContent> contents = new ArrayList<>();
    if (create.getAttachments() != null && !create.getAttachments().isEmpty()) {
      contents.addAll(create.getAttachments().stream()
          .map(file -> binaryContentRepository.save(BinaryContent.of(file))).toList());
    }

    Message message = messageRepository.save(
        new Message(create.getContent(), create.getChannelId(), create.getAuthorId(),
            contents.stream().map(BinaryContent::getId).toList()));

    channel.addMessage(message.getId());
    channelRepository.save(channel);

    return MessageDto.Detail.builder().id(message.getId()).channelId(message.getChannelId())
        .authorId(message.getAuthorId()).channelName(channel.getName()).authorName(author.getName())
        .content(message.getText()).createdAt(message.getCreatedAt())
        .updatedAt(message.getUpdatedAt())
        .attachmentIds(contents.stream().map(BinaryContent::getId).toList()).build();
  }

  @Override
  public MessageDto.Detail update(UpdateCommand update) {

    Message message = messageRepository.findById(update.getId())
        .orElseThrow(() -> new RuntimeException("Message not found"));

    User author = userRepository.findById(message.getAuthorId())
        .orElseThrow(() -> new RuntimeException("User not found"));
    Channel channel = channelRepository.findById(message.getChannelId())
        .orElseThrow(() -> new RuntimeException("Channel not found"));

    message.update(update.getContent());
    return MessageDto.Detail.builder().id(message.getId()).channelId(message.getChannelId())
        .authorId(message.getAuthorId()).channelName(channel.getName()).authorName(author.getName())
        .content(message.getText()).createdAt(message.getCreatedAt())
        .updatedAt(message.getUpdatedAt()).attachmentIds(message.getAttachmentIds()).build();
  }

  @Override
  public MessageDto.Detail findById(UUID id) {

    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Message not found"));

    User author = userRepository.findById(message.getAuthorId())
        .orElseThrow(() -> new RuntimeException("User not found"));
    Channel channel = channelRepository.findById(message.getChannelId())
        .orElseThrow(() -> new RuntimeException("Channel not found"));

    return MessageDto.Detail.builder().id(message.getId()).channelId(message.getChannelId())
        .authorId(message.getAuthorId()).channelName(channel.getName()).authorName(author.getName())
        .content(message.getText()).createdAt(message.getCreatedAt())
        .updatedAt(message.getUpdatedAt()).attachmentIds(message.getAttachmentIds()).build();
  }

  @Override
  public List<MessageDto.Detail> findAllByChannelId(UUID channelId) {

    List<Message> messages = messageRepository.findAllByChannelId(channelId);

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new RuntimeException("Channel not found"));

    List<User> users = messages.stream().map(Message::getAuthorId).distinct()
        .map(id -> userRepository.findById(id).orElse(null)).toList();

    return messages.stream().map(
            m -> MessageDto.Detail.builder().id(m.getId()).channelId(m.getChannelId())
                .authorId(m.getAuthorId()).channelName(channel.getName()).authorName(
                    users.stream().filter(u -> u.getId().equals(m.getAuthorId())).findFirst()
                        .orElse(null).getName()).content(m.getText()).createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt()).attachmentIds(m.getAttachmentIds()).build())
        .toList();
  }

  @Override
  public void delete(UUID id) {

    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Message not found"));

    messageRepository.delete(id);

    if (!message.getAttachmentIds().isEmpty()) {

      List<BinaryContent> contents = binaryContentRepository.findAllByIdIn(
          message.getAttachmentIds());

      contents.forEach(c -> binaryContentRepository.delete(c.getId()));
    }
  }

  @Override
  public void deleteAll() {
    messageRepository.deleteAll();
  }
}
