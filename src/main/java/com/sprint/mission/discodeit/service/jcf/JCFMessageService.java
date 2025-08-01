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
    public Message findMessage(UUID msgId) {
        Message message = messages.get(msgId.toString());
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
    public void updateMessage(UUID msgId, String newMsg) {
        Message msg = messages.get(msgId.toString());
        if (msg == null) {
            System.out.println("해당 ID의 메시지를 찾을 수 없습니다.");
            return;
        }

        msg.setContent(newMsg);
        System.out.println("메시지가 수정되었습니다: " + msg);
    }

    @Override
    public void deleteMessage(UUID msgId) {
        Message removed = messages.remove(msgId.toString());
        if (removed == null) {
            System.out.println("삭제할 메시지가 없습니다.");
        } else {
            System.out.println("메시지가 삭제되었습니다: " + removed);
        }
    }
}
