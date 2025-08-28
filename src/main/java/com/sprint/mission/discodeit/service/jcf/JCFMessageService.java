package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<String, Message> messages = new HashMap<>();

    @Override
    public void createMessage(String content, String channelName, String creatorUserId) {
        if (messages.containsKey(content)) {
            System.out.println("이미 존재하는 메시지입니다.");
            return;
        }
        Message message = new Message(content, channelName, creatorUserId);
        messages.put(content, message);
        System.out.println("메시지가 생성되었습니다: " + message);
    }

    @Override
    public Message findMessage(String content) {
        Message message = messages.get(content);
        if (message == null) {
            System.out.println("해당 메시지가 없습니다.");
        }
        return message;
    }

    @Override
    public List<Message> findAllMessages() {
        if (messages.isEmpty()) {
            System.out.println("메시지가 없습니다.");
        }
        return new ArrayList<>(messages.values());
    }

    @Override
    public void updateMessage(String content, String newContent) {
        Message message = messages.remove(content);
        if (message == null) {
            System.out.println("수정할 메시지가 없습니다.");
            return;
        }
        message.setContent(newContent);
        messages.put(newContent, message);
        System.out.println("메시지가 수정되었습니다: " + message);
    }

    @Override
    public void deleteMessage(String content) {
        Message removed = messages.remove(content);
        if (removed == null) {
            System.out.println("삭제할 메시지가 없습니다.");
        } else {
            System.out.println("메시지가 삭제되었습니다: " + removed);
        }
    }
}
