package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends Base{

    private final UUID userId;
    private final UUID channelId;
    private String content;
    private final List<UUID> files = new ArrayList<>();


    public Message(UUID userId, UUID channelId, String content) {
        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
        updateTimestamp();
    }

    public void addFile(UUID fileId) {
        if (fileId != null && !files.contains(fileId)) {
            files.add(fileId);
            updateTimestamp();
        }
    }

    public void removeFile(UUID fileId) {
        if (files.remove(fileId)) {
            updateTimestamp();
        }
    }
}
