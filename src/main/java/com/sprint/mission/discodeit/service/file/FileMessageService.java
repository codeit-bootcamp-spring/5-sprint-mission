package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.messageRepository = new FileMessageRepository();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message register(Message message) {
        if (isInvalid(message.getContent()))
            throw new IllegalArgumentException("메시지 등록에 실패했습니다.");

        User user = userService.findById(message.getUserId());
        Channel channel = channelService.findById(message.getChannelId());

        System.out.println("메시지 : " + message.getContent() + " 등록 성공.");
        return messageRepository.save(message);
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id).orElseThrow(() -> new RuntimeException("메시지에서 해당 " + id + "를 찾을 수 없습니다."));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID id, String newDescription) {
        if (isInvalid(newDescription))
            throw new IllegalArgumentException("새로운 메시지을 입력하세요.");

        Path path = Path.of("MESSAGE").resolve(id + ".ser");
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Message channel = (Message) ois.readObject();
            channel.setUpdatedAt(System.currentTimeMillis());
            channel.setContent(newDescription);
            return messageRepository.save(channel);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message delete(UUID id) {
        return messageRepository.delete(id);
    }

    public boolean isInvalid(String value) {
        return value == null || value.isBlank();
    }

}
