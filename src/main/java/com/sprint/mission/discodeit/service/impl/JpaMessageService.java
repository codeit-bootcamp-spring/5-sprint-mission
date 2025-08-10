package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Profile("prod")
public class JpaMessageService implements MessageService {
    @Override
    public Message send(UUID chatRoom, UUID senderId, String content, Set<String> files, UUID replyTo) {
        return null;
    }

    @Override
    public void updateContent(UUID messageId, String content) {

    }

    @Override
    public void updateFiles(UUID messageId, List<String> files) {

    }

    @Override
    public void printSenderAndContent(UUID messageId) {

    }
}
