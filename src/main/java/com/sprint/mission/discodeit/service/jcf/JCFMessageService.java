package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private Map<UUID, Message> data = new HashMap<>();
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(String content, UUID channelId, UUID authorId) {

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어 있을 수 없습니다.");
        }
        if (channelId == null) {
            throw new IllegalArgumentException("채널 ID는 필수입니다.");
        }
        if (authorId == null) {
            throw new IllegalArgumentException("작성자 ID는 필수입니다.");
        }

        if (channelService.findChannel(channelId).isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 채널 ID 입니다: " + channelId);
        }
        if (userService.findUser(authorId).isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 작성자 ID 입니다: " + authorId);
        }

        Message newMessage = new Message(content, channelId, authorId);
        data.put(newMessage.getId(), newMessage);
//        System.out.println("메시지 생성됨: " + newMessage);
        return newMessage;
    }

    @Override
    public Optional<Message> getMessage(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> getAllMessages() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message updateMessage(UUID id, String content) {
        Message message = data.get(id);
        if (message == null) {
            throw new IllegalArgumentException("변경 할 메시지가 없습니다.");
        }
        return message.update(content);
    }

    @Override
    public Message deleteMessage(UUID id) {

        if (id == null) {
            throw new IllegalArgumentException("삭제할 메시지 ID는 필수입니다.");
        }

        Message removedMessage = data.remove(id);

        if (removedMessage == null) {
            throw new NoSuchElementException(id + "에 해당하는 메시지를 찾을 수 없어 삭제할 수 없습니다.");
        }

        return removedMessage;
    }
}
