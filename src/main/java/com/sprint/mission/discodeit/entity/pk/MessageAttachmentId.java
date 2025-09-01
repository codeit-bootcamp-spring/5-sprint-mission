package com.sprint.mission.discodeit.entity.pk;

import java.io.Serializable;
import java.util.UUID;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MessageAttachmentId implements Serializable {
    private UUID message;
    private UUID attachment;
}