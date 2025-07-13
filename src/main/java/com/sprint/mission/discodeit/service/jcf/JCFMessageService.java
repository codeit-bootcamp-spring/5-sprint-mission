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
    public boolean addMessage(Message message) {
        if(message == null){
            return false;
        }
        data.add(message);
        return true;
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
    public Message updateMessage(Message updateMessage, UUID id) {
        return data.stream()
                .filter(existing -> existing.getId().equals(id))
                .findFirst()
                .map(existing -> {
                    existing.updateUpdatedAt(System.currentTimeMillis());
                    existing.updateContent(updateMessage.getContent());
                    existing.updatePinned(updateMessage.isPinned());
                    return existing;
                })
                .orElse(null);
    }

    @Override
    public Message deleteMessage(UUID messageId) {
        return data.stream()
                .filter(existing -> existing.getId().equals(messageId))
                .findFirst()
                .map(existing->{
                    data.remove(existing);
                    return existing;
                })
                .orElse(null);
    }

    @Override
    public void deleteAll() {
        data.clear();
    }
}
