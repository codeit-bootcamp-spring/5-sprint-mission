package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private final User user;
    private final Channel channel;
    private String message;
    private final Long createAt;
    private Long updateAt;

    public Message(UUID id, User user, Channel channel, String message, Long createAt) {
        this.id = id;
        this.user = user;
        this.channel = channel;
        this.message = message;
        this.createAt = createAt;
    }

    public UUID getId() {
        return id;
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

    public Long getCreateAt() {
        return createAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public void updateMessage(String message, Long updateAt) {
        this.message = message;
        this.updateAt = updateAt;
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
