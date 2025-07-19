package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Survey;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.File;
import java.util.List;
import java.util.UUID;

public class JcfMessageService extends JcfService<Message> implements MessageService {
  private static final JcfMessageService instance = new JcfMessageService();

  private JcfMessageService() {}

  public static JcfMessageService getInstance() {
    return instance;
  }

  @Override
  public boolean createMessage(Message message) {
    boolean exists = data.stream().anyMatch(m -> m.getId().equals(message.getId()));
    if (exists) {
      System.out.println("중복된 id가 존재합니다.");
      return false;
    }
    data.add(message);
    return true;
  }

  @Override
  public void updateContent(UUID messageId, String content) {
    update(messageId, m -> m.setContent(content));
  }

  @Override
  public void updateFiles(UUID messageId, List<File> files) {
    update(messageId, m -> m.setFiles(files));
  }

  @Override
  public void updateSurvey(UUID messageId, Survey survey) {
    update(messageId, m -> m.setSurvey(survey));
  }

  @Override
  public void addReply(UUID messageId, UUID replyId) {
    update(messageId, m -> m.addReply(replyId));
  }

  @Override
  public void removeReply(UUID messageId, UUID replyId) {
    update(messageId, m -> m.removeReply(replyId));
  }

  @Override
  public void clearReplies(UUID messageId) {
    update(messageId, Message::clearReplies);
  }
}
