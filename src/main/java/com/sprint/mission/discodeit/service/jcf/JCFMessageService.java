package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private final ChannelService channelService;
    private final UserService userService;

    public JCFMessageService(ChannelService channelService, UserService userService) {
        this.data = new HashMap<>();
        this.channelService = channelService;
        this.userService = userService;
    }

    @Override
    public Message create(UUID authorId, UUID channelId, String content) {
        try {
            channelService.find(channelId);
            userService.find(authorId);
        } catch (NoSuchElementException e) {
            throw e;
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("[!] 값이 null 이거나 비어있을 수 없습니다.");
        }
        Message message = new Message(authorId, channelId, content);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message find(UUID id) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("[!] 메시지가 없습니다");
        }
        return data.get(id);
    }

    @Override
    public List<Message> findAll() {
        if (data.isEmpty()) {
            throw new NoSuchElementException("[!] 결과가 없습니다.");
        }
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Message> searchByContent(String token) {
        return data.values().stream().filter(m -> (m.getContent().contains(token))).toList();
    }

    @Override
    public Message update(UUID id, UUID requestId, String newContent) {
        Message message = data.get(id);
        if (!data.get(id).getAuthorId().equals(requestId)) {
            throw new IllegalArgumentException("[!] 수정 권한이 없습니다.");
        }
        message.update(newContent);
        return message;
    }

    @Override
    public void delete(UUID id, UUID requestId) {
        if (!data.get(id).getAuthorId().equals(requestId)) {
            throw new IllegalArgumentException("[!] 삭제 권한이 없습니다.");
        }
        data.remove(id);
    }
}
