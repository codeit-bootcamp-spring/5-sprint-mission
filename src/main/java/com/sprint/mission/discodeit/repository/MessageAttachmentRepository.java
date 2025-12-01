package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageAttachment;
import com.sprint.mission.discodeit.entity.MessageAttachmentId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface MessageAttachmentRepository extends
    JpaRepository<MessageAttachment, MessageAttachmentId> {

    List<MessageAttachment> findByMessageIdOrderByOrderIndexAsc(@Param("messageId") UUID messageId);

    @EntityGraph(attributePaths = {"attachment"})
    List<MessageAttachment> findByMessageInOrderByOrderIndexAsc(Collection<Message> messages);
}
