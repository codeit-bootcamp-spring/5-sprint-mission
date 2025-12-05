package com.sprint.mission.discodeit.domain.messageattachment.repository;

import com.sprint.mission.discodeit.domain.message.entity.Message;
import com.sprint.mission.discodeit.domain.messageattachment.entity.MessageAttachment;
import com.sprint.mission.discodeit.domain.messageattachment.entity.MessageAttachmentId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface MessageAttachmentRepository extends
    JpaRepository<MessageAttachment, MessageAttachmentId> {

    @EntityGraph(attributePaths = {"attachment"})
    List<MessageAttachment> findByMessageIdOrderByOrderIndexAsc(UUID messageId);

    @EntityGraph(attributePaths = {"attachment"})
    List<MessageAttachment> findByMessageInOrderByOrderIndexAsc(Collection<Message> messages);
}
