package com.codeit.mission.discodeit.dto.message;

import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class MessageCreateRequest {

    private final String content;
    private final UUID channelId;
    private final UUID authorId;
    private final List<AttachmentRequest> attachments;

    public MessageCreateRequest(String content, UUID channelId, UUID authorId, List<AttachmentRequest> attachments) {
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachments = attachments;
    }

    public MessageCreateRequest(String content, UUID channelId, UUID authorId) {
        this(content, channelId, authorId, null);
    }
}
