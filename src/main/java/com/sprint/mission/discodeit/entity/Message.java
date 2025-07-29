package com.sprint.mission.discodeit.entity;

import java.io.Serializable;

public class Message extends Base implements Serializable {

    private final User sender; //발신자는 변경할 수 없음
    private final Channel channel; //채널은 변경할 수 없음
    private String content;

    public Message(User sender, Channel channel, String content) {
        this.sender = sender;
        this.channel = channel;
        this.content = content;
    }

    public Channel getChannel() {
        return channel;
    }

    public User getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public void updateContent(String newContent) {
        if (newContent == null || newContent.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어 있을 수 없습니다.");
        }
        this.content = newContent;
        updateTimestamp();
    }


    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender.getName() +
                ", channel=" + channel.getName() +
                ", content='" + content + '\'' +
                ", createdAt=" + getCreatedAtFormatted() +
                '}';
    }

    // toString을 간결한 로그 스타일로
    public String getFormattedMessage() {
        return "\n" + getCreatedAtFormatted() + "   " + sender.getName() + ": " + content + "\n";
    }


}
