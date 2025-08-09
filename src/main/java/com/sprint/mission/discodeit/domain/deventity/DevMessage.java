package com.sprint.mission.discodeit.domain.deventity;


import com.sprint.mission.discodeit.util.Validators;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
public class DevMessage extends DevBaseEntity {

    private final UUID sender;
    private String content;
    private final Set<String> files = new LinkedHashSet<>();
    private final UUID replyTo;
    private final UUID chatRoom;

    public DevMessage(UUID chatRoom, UUID senderId, String content, Set<String> files, UUID replyTo) {
        this.chatRoom = Objects.requireNonNull(chatRoom, "Chat room id must not be null.");
        this.sender = Objects.requireNonNull(senderId, "Sender id must not be null.");
        setContent(content);
        setFiles(files);
        this.replyTo = replyTo;
    }

    public DevMessage(UUID chatRoom, UUID senderId, String content, Set<String> files) {
        this(chatRoom, senderId, content, files, null);
    }

    public DevMessage(UUID chatRoom, UUID senderId, String content) {
        this(chatRoom, senderId, content, null, null);
    }

    public void setContent(String content) {
        this.content = Validators.validateMessageContent(content);
        touch();
    }

    public Set<String> getFiles() {
        return Collections.unmodifiableSet(files);
    }

    public void setFiles(Set<String> files) {
        boolean changed = false;
        this.files.clear();
        if (files != null && !files.isEmpty()) {
            for (String file : files) Validators.validateUri(file);
            this.files.addAll(files);
            changed = true;
        }
        if (changed) touch();
    }

    @Override
    public String toString() {
        return String.format("Message[chatRoom=%s, sender=%s, content='%s', files=%d]",
                chatRoom, sender, content, files.size());
    }
}
