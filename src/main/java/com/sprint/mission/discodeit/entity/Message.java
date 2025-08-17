package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String content;
    private UUID channelId;
    private UUID authorId;
    private List<UUID> attachmentIds = new ArrayList<>();

    // Simple constructor for testing serialization
    public Message() {
        super();
    }

    public Message(UUID id, Instant createdAt, Instant updatedAt, String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
        super(id, createdAt, updatedAt);
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = attachmentIds;
    }

    public void update(String content) {
        boolean anyValueUpdated = false;
        if (content != null && !content.equals(this.content)) {
            this.content = content;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            setUpdatedAt(Instant.now());
        }
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", channelId=" + channelId +
                ", authorId=" + authorId +
                ", content='" + content + "'" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseEntity that = (BaseEntity) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
