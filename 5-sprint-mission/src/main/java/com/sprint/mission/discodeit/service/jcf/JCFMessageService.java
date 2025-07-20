package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {

    final Map<UUID, Message> mdata = new HashMap<>();

    private static JCFMessageService instance;

    private JCFMessageService() {}

    public static JCFMessageService getInstance() {
        if (instance == null) {
            instance = new JCFMessageService();
        }
        return instance;
    }

    @Override
    public Message delete(UUID id) {
        if (!mdata.containsKey(id)) {
            throw new NoSuchElementException("메시지를 찾을 수 없습니다.");
        }
        Message deletedMessage = mdata.get(id);
        mdata.remove(id);
        return deletedMessage;
    }

    @Override
    public Message update(UUID id, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용이 비어있습니다.");
        }
        if (!mdata.containsKey(id)) {
            throw new NoSuchElementException("메시지를 찾을 수 없습니다.");
        }
        Message message = mdata.get(id);
        message.update(content);
        return message;
    }

    @Override
    public Message find(UUID id) {
        if (!mdata.containsKey(id)) {
            throw new NoSuchElementException("메세지를 찾을 수 없습니다.");
        }
        return mdata.get(id);
    }

    @Override
    public List<Message> findAll() {
        if (mdata.isEmpty()) {
            throw new NoSuchElementException("목록이 비어 있습니다.");
        }
        return new ArrayList<>(mdata.values());
    }

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("메세지가 비어 있습니다.");
        }
        Message message = new Message(content, authorId, channelId);
        mdata.put(message.getId(), message);
        System.out.println("[ " + channelId + " ] " + authorId + " : " + content);
        return message;
    }
}
