package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.MessageAttachment;
import com.sprint.mission.discodeit.entity.MessageAttachmentId;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageAttachmentRepository extends
    JpaRepository<MessageAttachment, MessageAttachmentId> {

    @Query("""
            SELECT ma.attachment
            FROM MessageAttachment ma
            WHERE ma.message.id = :messageId
            ORDER BY ma.orderIndex
        """)
    List<BinaryContent> findAttachmentsByMessageId(@Param("messageId") UUID messageId);

    @Query("""
            SELECT ma.message.id as messageId, ma.attachment
            FROM MessageAttachment ma
            WHERE ma.message.id IN :messageIds
            ORDER BY ma.message.id, ma.orderIndex
        """)
    List<MessageBinaryRow> findBinariesByMessageIds(
        @Param("messageIds") Collection<UUID> messageIds);

    interface MessageBinaryRow {

        UUID getMessageId();

        BinaryContent getAttachment();
    }
}
