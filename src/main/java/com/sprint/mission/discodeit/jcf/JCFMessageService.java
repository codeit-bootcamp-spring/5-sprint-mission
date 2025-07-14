package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {

    Map<UUID, Message> messages = new HashMap<UUID, Message>();

    // 메시지 생성
    @Override
    public void create(Message message) {
        messages.put(message.getId(), message);
    }

    // 삭제되지 않은 메시지 조회
    @Override
    public Message findById(UUID id) {
        Message message = messages.get(id);
        if (message != null && !message.isDeleted()) {
            return message;
        }
        return null;
    }

    // 삭제되지 않은 메시지 리스트 반환
    @Override
    public List<Message> findAll() {
        List<Message> result = new ArrayList<>();
        for (Message message : messages.values()) {
            if (!message.isDeleted()) {
                result.add(message);
            }
        }
        return result;
    }

    // 메시지 정보 업데이트 (삭제된 메세지는 제외)
    @Override
    public void update(UUID id, String content) {
        Message message = messages.get(id);
        if (message != null && !message.isDeleted()) {
            message.update(content);
        }
    }

    // 메시지 삭제 (소프트 삭제 적용)
    @Override
    public void delete(UUID id) {
        Message message = messages.get(id);
        if (message != null) {
            message.delete();
        }
    }

    // 삭제 여부와 무관하게 메시지 존재 여부 확인
    public boolean exists(UUID id) {
        return messages.containsKey(id);
    }
}
