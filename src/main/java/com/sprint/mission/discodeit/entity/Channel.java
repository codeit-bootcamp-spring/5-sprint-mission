package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    // 내가 디스코드에서 필요한 필드를 추가적으로 설계하는 자리
    private String channelName; // 채널의 이름
    private String channelIntroduction;
    private ChannelType type;

    public enum ChannelType{
        CHAT(1), VOICE(2);

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
    }

    public Channel(String channelName, String channelIntroduction, ChannelType type) {
        id = UUID.randomUUID();
        this.channelName = channelName;
        this.channelIntroduction = channelIntroduction;
        this.type = type;
        createdAt = System.currentTimeMillis();
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

    public void updateId(UUID id) {
        this.id = id;
        this.updatedAt = System.currentTimeMillis();
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

    public void updateChannelType(ChannelType channelType) {
        this.type = channelType;
        this.updatedAt = System.currentTimeMillis();
    }
}
