package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageService implements MessageService {

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageRepository messageRepository;

    public FileMessageService(UserService userService, ChannelService channelService, FileMessageRepository messageRepository) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageRepository = messageRepository;
    }

    @Override
    public Message createMessage(UUID senderId, UUID channelId, String name, String title, String content) {
        if (userService.readUser(senderId).isEmpty() | channelService.readChannel(channelId).isEmpty()) {
            return null;
        }
        Message message = new Message(senderId, channelId, name, title, content);
        messageRepository.save(message);
        return message;
    }

    @Override
    public Optional<Message> readMessage(UUID id) {
        if (messageRepository.existsById(id)) {
            System.out.println("조회 성공: " + messageRepository.findById(id).get());
            return messageRepository.findById(id);
        }
        System.out.println("등록된 회원이 없습니다.");
        return Optional.empty();
    }

    @Override
    public List<Message> readAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    public Message updateMessage(Message message) {
        if (messageRepository.existsById(message.getId())) {
            System.out.println("수정 완료: " + message);
            return messageRepository.update(message.getId(), message);
        } else {
            System.out.println("수정 실패");
            return null;
        }
    }

    @Override
    public void deleteMessage(UUID id) {
        messageRepository.deleteById(id);
    }
}
