package com.sprint.mission.discodeit.service.dto.message;

import java.util.List;
import java.util.UUID;

public class MessageCreateRequest {
    private final String content;
    private final UUID channelId;
    private final UUID authorId;
    private final List<AttachmentCreate> attachments;

    public MessageCreateRequest(String content, UUID channelId, UUID authorId, List<AttachmentCreate> attachments) {
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachments = attachments;
    }
    public String getContent() { return content; }
    public UUID getChannelId() { return channelId; }
    public UUID getAuthorId() { return authorId; }
    public List<AttachmentCreate> getAttachments() { return attachments; }

    public static class AttachmentCreate {
        private final byte[] data;
        private final String filename;
        private final String contentType;

        public AttachmentCreate(byte[] data, String filename, String contentType) {
            this.data = data;
            this.filename = filename;
            this.contentType = contentType;
        }
        public byte[] getData() { return data; }
        public String getFilename() { return filename; }
        public String getContentType() { return contentType; }
    }
}
