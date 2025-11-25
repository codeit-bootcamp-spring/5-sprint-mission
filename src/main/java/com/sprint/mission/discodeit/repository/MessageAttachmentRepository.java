package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageAttachment;
import com.sprint.mission.discodeit.entity.MessageAttachmentId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface MessageAttachmentRepository extends
    JpaRepository<MessageAttachment, MessageAttachmentId> {

    @Query("""
            SELECT ma.attachment
            FROM MessageAttachment ma
            WHERE ma.message.id = :messageId
            ORDER BY ma.orderIndex
        """)
    List<BinaryContent> findAttachmentsByMessageId(@Param("messageId") UUID messageId);

    @EntityGraph(attributePaths = {"attachment"})
    List<MessageAttachment> findAllByMessageIn(Collection<Message> messages);

    @Modifying
    @Query(
        value = """
            DELETE FROM message_attachments ma
            WHERE ma.message_id = :messageId;
            DELETE FROM binary_contents bc
            WHERE bc.id IN (
                SELECT ma.attachment_id
                FROM message_attachments ma
                WHERE ma.message_id = :messageId
            );
            """,
        nativeQuery = true
    )
    int deleteAllByMessageId(@Param("messageId") UUID messageId);
}
