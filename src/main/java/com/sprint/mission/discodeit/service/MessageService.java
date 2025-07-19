package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Survey;
import java.io.File;
import java.util.List;
import java.util.UUID;

public interface MessageService extends BaseService<Message> {
  boolean create(Message message);

  void updateContent(UUID messageId, String content);

  void updateFiles(UUID messageId, List<File> files);

  void updateSurvey(UUID messageId, Survey survey);

  void addReply(UUID messageId, UUID replyId);

  void removeReply(UUID messageId, UUID replyId);
}
