package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends Base{

    private final User sender; //발신자는 변경할 수 없음
    private final Channel channel; //채널은 변경할 수 없음
    private String content;
    private final List<UUID> files = new ArrayList<>();


    public Message(User sender, Channel channel, String content) {
        this.sender = sender;
        this.channel = channel;
        this.content = content;
    }

    public void updateContent(String newContent) {
        if (newContent == null || newContent.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어 있을 수 없습니다.");
        }
        this.content = newContent;
        updateTimestamp();
    }

    public void updateFile(UUID fileId) {
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

    @Override
    public String toString() {
        return String.format(
                "\n작성자: %-10s  채널: %-10s  내용: %-10s  보낸시간: %-10s",
                sender.getName(), channel.getName(), getContent(), getCreatedAtFormatted()
        );
    }
}
