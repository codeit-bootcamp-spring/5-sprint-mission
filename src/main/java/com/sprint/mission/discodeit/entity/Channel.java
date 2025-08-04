package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private final UUID id;
    private Long createdAt;
    private Long updatedAt;

    // 변경점
    // 내가 디스코드에서 필요한 필드를 추가적으로 설계하는 자리
    private String channelName; // 채널의 이름
    private String channelIntroduction; // 채널의 소개
    private final ChannelType type; // 채널의 형태(공개/비공개, 문자채팅/음성채팅)

    public enum ChannelType{
        PUBLICCHAT(1), PUBLICVOICE(2), PRIVATECHAT(3), PRIVATEVOICE(4);

        private final int typeValue;

        ChannelType(int typeValue) {
            this.typeValue = typeValue;
        }

        public int getTypeValue() {
            return typeValue;
        }

        public static ChannelType fromValue(int typeValue) {
            for (ChannelType type : ChannelType.values()) {
                if (type.getTypeValue() == typeValue) {
                    return type;
                }
            }
            throw new IllegalArgumentException("유효하지 않은 타입입니다 : " + typeValue);
        }

        public static ChannelType fromName(String channelName) {
            for (ChannelType type : ChannelType.values()) {
                if (type.name().equalsIgnoreCase(channelName)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("유효하지 않은 채널 이름입니다 : " + channelName);
        }
    }

    public Channel() { // 기본 생성자
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.type = ChannelType.PUBLICCHAT; // 기본설정 공개 채팅방
    }

    public Channel(String channelName, String channelIntroduction, ChannelType type) {
        id = UUID.randomUUID();
        this.channelName = channelName;
        this.channelIntroduction = channelIntroduction;
        this.type = type;
        createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
    }

    public Channel(String channelName, String channelIntroduction, int typeValue) {
        // Int로 channelType 지정하는 오버로딩 메서드
        this(channelName, channelIntroduction, ChannelType.fromValue(typeValue));
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelIntroduction() {
        return channelIntroduction;
    }

    public ChannelType getType() {
        return type;
    }

    public void updateUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateChannelIntroduction(String channelIntroduction) {
        this.channelIntroduction = channelIntroduction;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", channelName='" + channelName + '\'' +
                ", channelIntroduction='" + channelIntroduction + '\'' +
                ", type=" + type +
                '}';
    }
}
