package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.MessageAttachment;
import com.sprint.mission.discodeit.entity.MessageAttachmentId;
import com.sprint.mission.discodeit.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageAttachmentRepository extends
    JpaRepository<MessageAttachment, MessageAttachmentId> {

    default MessageAttachment getOrThrow(MessageAttachmentId id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "ChannelParticipant with id %s not found".formatted(id))
        );
    }
}
