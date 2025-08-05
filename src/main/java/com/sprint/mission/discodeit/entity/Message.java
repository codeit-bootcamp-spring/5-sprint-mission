package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID authorUserId;
    private String content;

    public Message(
            String content, UUID authorUserId
    ) {
        super();
        this.authorUserId = authorUserId;
        this.content = content;
    }

    public UUID getAuthorUserId() {
        return authorUserId;
    }

    public String getContent() {
        return content;
    }

    public void updateContent(String content) {
        if(!this.content.equals(content)){
            this.content = content;
            super.updateUpdatedAt();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(authorUserId, message.authorUserId) && Objects.equals(content, message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorUserId, content);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Message{");
        sb.append("authorUserId=").append(authorUserId);
        sb.append(", content='").append(content).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
