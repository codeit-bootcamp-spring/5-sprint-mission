package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Survey;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private static final JCFMessageService instance = new JCFMessageService();

    private final List<Message> data;

    private JCFMessageService() {
        data = new ArrayList<Message>();
    }

    public static JCFMessageService getInstance() {
        return instance;
    }

    @Override
    // @VisibleForTesting
    public void reset() {
        JCFMessageService.getInstance().data.clear();
    }

    @Override
    public boolean createMessage(Message message) {
        boolean exists = data.stream()
                .anyMatch(m -> m.getId().equals(message.getId()));
        if (exists) {
            System.out.println("중복된 id가 존재합니다.");
            return false;
        }
        data.add(message);
        return true;
    }

    @Override
    public Message findById(UUID id) {
        return data.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Message> findAll() {
        return data;
    }

    @Override
    public void updateContent(Message message, String content) {
        data.stream()
                .filter(m -> m.getId().equals(message.getId()))
                .findFirst()
                .ifPresent(m -> {
                    m.setContent(content);
                    m.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateFiles(Message message, List<File> files) {
        data.stream()
                .filter(m -> m.getId().equals(message.getId()))
                .findFirst()
                .ifPresent(m -> {
                    m.setFiles(files);
                });

    }

    @Override
    public void updateSurvey(Message message, Survey survey) {
        data.stream()
                .filter(m -> m.getId().equals(message.getId()))
                .findFirst()
                .ifPresent(m -> {
                    m.setSurvey(survey);
                    m.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateReplies(Message message, List<Message> replies) {
        data.stream()
                .filter(m -> m.getId().equals(message.getId()))
                .findFirst()
                .ifPresent(m -> {
                    m.setReplies(replies);
                    m.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void deleteById(UUID id) {
        data.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .ifPresent(data::remove);
    }
}
