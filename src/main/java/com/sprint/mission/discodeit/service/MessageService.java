package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Survey;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface MessageService extends Service {
    boolean createMessage(Message message);

    Message findById(UUID id);

    List<Message> findAll();

    void updateContent(Message message, String content);

    void updateFiles(Message message, List<File> files);

    void updateSurvey(Message message, Survey survey);

    void updateReplies(Message message, List<Message> replies);

    void deleteById(UUID id);
}
