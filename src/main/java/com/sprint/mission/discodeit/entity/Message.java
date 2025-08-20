package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class Message extends BaseEntity {
    private String text;
    private final UUID channelId;
    private final UUID authorId;
    private final List<UUID> attachmentIds;

    public Message(String text, UUID channelId, UUID authorId) {
        super();
        this.text = text;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = new ArrayList<>();
    }

    public Message(String text, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
        super();
        this.text = text;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = attachmentIds != null ? attachmentIds : new ArrayList<>();
    }

    public void update(String newText) {
        boolean anyValueUpdated = false;

        if (newText != null && !newText.equals(this.text)) {
            this.text = newText;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            updateTimestamp();
        }
    }
}
