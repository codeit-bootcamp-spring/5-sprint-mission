package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Survey;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileMessageService extends BaseFileService<Message> implements MessageService {
    private final UserService userService;

    public FileMessageService(UserService userService) {
        super("messages.ser");
        this.userService = userService;
    }

    @Override
    public Message save(Message message) {
        if (findById(message.getId()).isPresent()) {
            throw new IllegalArgumentException("중복된 id가 존재합니다.");
        }

        User sender = userService.getOrThrow(message.getSenderId());
        if (sender.isBanned() || sender.isDeactivated()) {
            throw new IllegalStateException("유저를 찾을 수 없습니다.");
        }

        return super.save(message);
    }

    @Override
    public void updateContent(UUID messageId, String content) {
        update(messageId, m -> m.setContent(content));
    }

    @Override
    public void updateFiles(UUID messageId, List<String> files) {
        update(messageId, m -> m.setFiles(files));
    }

    @Override
    public void updateSurvey(UUID messageId, Survey survey) {
        update(messageId, m -> m.setSurvey(survey));
    }

    @Override
    public void printSenderAndContent(UUID messageId) {
        String content = getOrThrow(messageId).getContent();
        User sender = userService.getOrThrow(getOrThrow(messageId).getSenderId());
        System.out.println(sender.getGlobalName() + ": " + content);
    }
}
