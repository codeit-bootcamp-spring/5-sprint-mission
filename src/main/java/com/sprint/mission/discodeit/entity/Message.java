package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;


public class Message extends BaseEntity {
    private final User user;
    private final Channel channel;
    private String message;

    public Message(User user, Channel channel, String message) {
        this(UUID.randomUUID(), user, channel, message, Instant.now().getEpochSecond());
    }
    public Message(UUID id, User user, Channel channel, String message, Long createAt) {
        super(id, createAt);
        this.user = user;
        this.channel = channel;
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    public void updateMessage(String message) {
        this.message = message;
        super.updateTimeStamp();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("보낸 사람: ").append(user.getNickname()).append('\n')
                .append("보낸 채널: ").append(channel.getChannelName()).append('\n')
                .append("보낸 내용: ").append(message).append('\n')
                .append("보낸 시간: ").append(createAt).append('\n')
                .append("수정 시간:  ").append(updateAt).append('\n');

        return sb.toString();
    }
}
