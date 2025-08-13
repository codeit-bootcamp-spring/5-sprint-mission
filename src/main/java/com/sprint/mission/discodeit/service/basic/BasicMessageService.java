package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final UserService userService;
    private final ChannelService channelService;
    private final BinaryContentService binaryContentService;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponse.detail create(MessageRequest.create dto) {

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Channel channel = channelRepository.findById(dto.channelId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        Message message = new Message(dto.userId(), dto.channelId(), dto.content());

        userService.findById(dto.userId()); // 존재하는 사용자인지
        channelService.findById(dto.channelId()); // 존재하는 채널인지

        // 첨부한 파일이 존재하면
        if (dto.files() != null && !dto.files().isEmpty()) {

            if (dto.files().size() > 3) {
                throw new IllegalArgumentException("첨부파일은 최대 3개까지만 업로드할 수 있습니다.");
            }

            for (MultipartFile file : dto.files()) {
                BinaryContent binary = binaryContentService.convertMultipartFile(file);
                binaryContentRepository.save(binary);
                message.addFile(binary.getId());
            }
        }

            List<String> fileUrls = message.getFiles().stream()
                    .map(id -> "/binary/" + id).toList();

            messageRepository.save(message);

            return MessageResponse.detail.builder()
                    .id(message.getId())
                    .userId(user.getId())
                    .userName(user.getName())
                    .channelId(channel.getId())
                    .channelName(channel.getName())
                    .content(dto.content())
                    .fileUrls(fileUrls)
                    .createdAt(message.getCreatedAtFormatted())
                    .build();

    }

    @Override
    public MessageResponse.updated update(MessageRequest.update dto) {

        Message message = messageRepository.findById(dto.id())
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        message.updateContent(dto.content());
        Message newMessage = messageRepository.save(message);
        return MessageResponse.updated.builder()
                .id(message.getId())
                .content(newMessage.getContent())
                .createdAt(newMessage.getCreatedAtFormatted())
                .updateAt(newMessage.getUpdatedAtFormatted())
                .build();
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));
    }

    public List<Message> findByChannel(UUID channelId) {
        return messageRepository.findByChannel(channelId);
    }

    @Override
    public List<Message> findByUserId(UUID userId) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findByContent(String content) {
        List<Message> result = new ArrayList<>();
        for (Message message : messageRepository.findAll()) {
            if (message.getContent().contains(content)) {
                result.add(message);
            }
        }
        return result;
    }



    @Override
    public void attachFile(UUID messageId, UUID fileId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        message.addFile(fileId); // 도메인 객체 내 로직 호출
        messageRepository.save(message); // 변경사항 저장
    }

    @Override
    public void detachFile(UUID messageId, UUID fileId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        message.removeFile(fileId);
        messageRepository.save(message);
    }

    @Override
    public boolean delete(UUID id) {
        return messageRepository.deleteById(id);
    }
}
