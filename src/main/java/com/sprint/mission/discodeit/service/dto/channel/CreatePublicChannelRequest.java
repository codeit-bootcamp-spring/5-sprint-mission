package com.sprint.mission.discodeit.service.dto.channel;

public class CreatePublicChannelRequest { // 클래스 선언
    public final String name; // 채널 이름
    public final String description; // 설명

    public CreatePublicChannelRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
