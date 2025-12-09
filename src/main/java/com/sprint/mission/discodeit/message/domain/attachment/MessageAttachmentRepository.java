package com.sprint.mission.discodeit.message.domain.attachment;

import com.sprint.mission.discodeit.message.domain.Message;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface MessageAttachmentRepository extends
    JpaRepository<MessageAttachment, MessageAttachmentId> {

    @EntityGraph(attributePaths = {"attachment"})
    List<MessageAttachment> findAllWithAttachmentByMessageIdOrderByOrderIndexAsc(UUID messageId);

    @EntityGraph(attributePaths = {"attachment"})
    List<MessageAttachment> findAllWithAttachmentByMessageInOrderByOrderIndexAsc(Collection<Message> messages);

    @Query("""
        SELECT ma.id
        FROM MessageAttachment ma
        WHERE ma.id.messageId in :messageIds
        """)
    List<UUID> findIdsByMessageIdIn(Collection<UUID> messageIds);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
        DELETE FROM MessageAttachment ma
        WHERE ma.id.messageId in :messageIds
        """)
    int deleteAllByMessageIdIn(Collection<UUID> messageIds);
}
