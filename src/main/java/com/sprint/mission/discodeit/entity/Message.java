package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID authorUserId;
    private final UUID channelId;
    private final List<UUID> attachmentIds = new ArrayList<>();
    private String content;


    public Message(
            String content, UUID authorUserId, UUID channelId
    ) {
        super();
        this.authorUserId = authorUserId;
        this.channelId = channelId;

        this.content = content;
    }

    public void addAttachmentId(UUID attachmentId) {
        this.attachmentIds.add(attachmentId);
    }

    public void removeAttachmentId(UUID attachmentId) {
        if(!this.attachmentIds.contains(attachmentId)){
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
        this.attachmentIds.remove(attachmentId);
    }


    public void updateContent(String content) {
        if(!this.content.equals(content)){
            this.content = content;
            super.updateUpdatedAt();
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Message{");
        sb.append("authorUserId=").append(authorUserId);
        sb.append(", channelId=").append(channelId);
        sb.append(", attachmentIds=").append(attachmentIds);
        sb.append(", content='").append(content).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(authorUserId, message.authorUserId) && Objects.equals(channelId, message.channelId) && Objects.equals(attachmentIds, message.attachmentIds) && Objects.equals(content, message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorUserId, channelId, attachmentIds, content);
    }
}
