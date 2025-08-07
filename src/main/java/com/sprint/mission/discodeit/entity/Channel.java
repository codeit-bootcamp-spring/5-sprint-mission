package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Channel extends BaseEntity {
    private String channelName;
    private String channelDescription;

    public Channel(String channelName, String channelDescription) {
        this(UUID.randomUUID(), channelName, channelDescription, Instant.now());
    }

    public Channel(UUID id, String channelName, String channelDescription, Instant createAt) {
        super(id, createAt);
        this.channelName = channelName;
        this.channelDescription = channelDescription;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("채널 아이디: ").append(id).append("\n")
                .append("채널 이름: ").append(this.channelName).append("\n")
                .append("채널 설명: ").append(this.channelDescription).append("\n")
                .append("채널 생성일: ").append(this.createAt).append("\n")
                .append("채널 변경일: ").append(this.updateAt).append("\n");

        return sb.toString();
    }
}
