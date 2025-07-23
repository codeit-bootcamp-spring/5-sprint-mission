package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private static final List<Message> data = new ArrayList<>();

    private static JCFMessageService instance;

    private JCFMessageService() {}

    public static JCFMessageService getInstance() {
        if (instance == null) {
            instance = new JCFMessageService();
        }
        return instance;
    }

    @Override
    public void addMessage(Message message) {
        if(message == null){
            return;
        }
        data.add(message);
    }

    @Override
    public List<Message> getMessages() {
        return data;
    }

    @Override
    public Message getMessageById(UUID messageId) {
        return data.stream().filter(u->u.getId().equals(messageId)).findFirst().orElse(null);
    }

    @Override
    public void updateMessage(Message updateMessage, UUID id) {
        data.stream()
                .filter(existing -> existing.getId().equals(id))
                .findFirst()
                .map(existing -> {
                    existing.updateContent(updateMessage.getContent());
                    return existing;
                });
    }

    @Override
    public void deleteMessage(UUID messageId) {
        data.stream()
                .filter(existing -> existing.getId().equals(messageId))
                .findFirst()
                .map(existing -> {
                    data.remove(existing);
                    return existing;
                });
    }

    @Override
    public void deleteAll() {
        data.clear();
    }
}
