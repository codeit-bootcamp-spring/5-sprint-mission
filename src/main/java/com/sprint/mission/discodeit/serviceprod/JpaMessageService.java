package com.sprint.mission.discodeit.serviceprod;

import com.sprint.mission.discodeit.domain.entityprod.ProdMessage;
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
public class JpaMessageService {
    public ProdMessage send(UUID chatRoom, UUID senderId, String content, Set<String> files, UUID replyTo) {
        return null;
    }

    public void updateContent(UUID messageId, String content) {

    }

    public void updateFiles(UUID messageId, List<String> files) {

    }

    public void printSenderAndContent(UUID messageId) {

    }
}
