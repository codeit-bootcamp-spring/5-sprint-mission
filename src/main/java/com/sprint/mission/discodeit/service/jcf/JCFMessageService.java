package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;

public class JCFMessageService implements MessageService {

    private final List<Message> data;

    public JCFMessageService() {
        data = new ArrayList<>();
    }

    @Override
    public void addMessage(Message message) {
        data.add(message);
    }

    @Override
    public void updateMessage(Message message) {
        int i = data.indexOf(message);
        data.set(i, message);
    }

    @Override
    public void deleteMessage(Message message) {
        data.remove(message);
    }

    @Override
    public Message getMessage(int i) {
        return data.get(i);
    }

    @Override
    public List<Message> getAllMessages() {
        return data;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JCFMessageService{");
        sb.append("data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
