package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.UUID;

public interface MessageRepository extends BaseRepository<Message> {
    void updateContent(UUID messageId, String content);

    void updateFiles(UUID messageId, java.util.List<String> files);

    void updateSurvey(UUID messageId, com.sprint.mission.discodeit.entity.Survey survey);
}
