package com.sprint.mission.discodeit.message.domain.attachment;

import com.sprint.mission.discodeit.message.domain.Message;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface MessageAttachmentRepository extends
    JpaRepository<MessageAttachment, MessageAttachmentId> {

    @EntityGraph(attributePaths = {"attachment"})
    List<MessageAttachment> findAllWithAttachmentByMessageIdOrderByOrderIndexAsc(UUID messageId);

    @EntityGraph(attributePaths = {"attachment"})
    List<MessageAttachment> findAllWithAttachmentByMessageInOrderByOrderIndexAsc(Collection<Message> messages);

    List<MessageAttachment> findAllByMessageIdIn(Collection<UUID> messageIds);
}
