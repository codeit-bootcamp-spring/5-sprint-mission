package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Survey;

import java.util.List;
import java.util.UUID;

public interface MessageService extends BaseService<Message> {
    void updateContent(UUID messageId, String content);

    void updateFiles(UUID messageId, List<String> files);

    void updateSurvey(UUID messageId, Survey survey);

    void printSenderAndContent(UUID messageId);
}
