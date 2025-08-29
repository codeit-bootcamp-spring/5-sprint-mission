package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public record MessageAttachmentId(
    UUID messageId,
    UUID attachmentId
) implements Serializable {

}