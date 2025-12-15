package com.sprint.mission.discodeit.message.domain.attachment;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface MessageAttachmentRepository extends
    JpaRepository<MessageAttachment, MessageAttachmentId> {

    @EntityGraph(attributePaths = {"attachment"})
    List<MessageAttachment> findAllWithAttachmentByMessageIdOrderByOrderIndexAsc(UUID messageId);

    @EntityGraph(attributePaths = {"attachment"})
    List<MessageAttachment> findAllWithAttachmentByMessageIdInOrderByOrderIndexAsc(Collection<UUID> messageIds);

    @Query("""
        SELECT ma.id.attachmentId
        FROM MessageAttachment ma
        WHERE ma.id.messageId IN :messageIds
        """)
    Set<UUID> findAttachmentIdSetByMessageIdIn(Collection<UUID> messageIds);

    @Query("""
        SELECT ma.id.attachmentId
        FROM MessageAttachment ma
        WHERE ma.id.messageId = :messageId
        """)
    Set<UUID> findAttachmentIdSetByMessageId(UUID messageId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        DELETE FROM MessageAttachment ma
        WHERE ma.id.messageId IN :messageIds
        """)
    int deleteAllByMessageIdIn(Collection<UUID> messageIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        DELETE FROM MessageAttachment ma
        WHERE ma.attachment.id IN :attachmentIds
        """)
    int deleteAllByAttachmentIdIn(Collection<UUID> attachmentIds);
}
