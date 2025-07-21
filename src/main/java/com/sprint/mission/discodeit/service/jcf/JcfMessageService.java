package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Survey;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import java.io.File;
import java.util.List;
import java.util.UUID;

public class JcfMessageService extends BaseJcfService<Message> implements MessageService {
  private static final JcfMessageService instance = new JcfMessageService();

  private final UserService userService;

  private JcfMessageService() {
    this.userService = JcfUserService.getInstance();
  }

  public static JcfMessageService getInstance() {
    return instance;
  }

  @Override
  public boolean create(Message message) {
    if (findById(message.getId()) != null) {
      throw new IllegalArgumentException("중복된 id가 존재합니다.");
    }

    User sender = userService.getIfExists(message.getSenderId());
    if (sender.isBanned() || sender.isDeactivated()) {
      throw new IllegalStateException("유저를 찾을 수 없습니다.");
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
}
