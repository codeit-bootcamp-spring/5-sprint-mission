package com.sprint.mission.discodeit.entity;

import java.util.UUID; // UUID를 사용하기 위한 import
import java.io.Serializable; // 직렬화 기능 sprint-2

//채널 도메인 모델 클래스
// 채널 고유 ID, 생성 시간, 수정 시간, 이름, 채널 설명을 관리한다.
// implements Serializable 직렬화 상속 sprint-2
public class Channel implements Serializable {
    private UUID channelId; //객체를 식별하기 위한 고유 id (UUID type)
    private Long createdAt; // 객체 생성 시간 저장 (Unix time stamp, Long type)
    private Long updatedAt; // 객체 수정 시간 (Unix time stamp, Long type)
    private String channelName; // 채널 이름
    private String description; // 채널 설명

    // Channel 객체의 생성자입니다.
    // id와 createdAt은 생성 시점에 초기화되며, 다른 필드들은 파라미터를 통해 초기화됩니다.
    // @param channelName 채널 이름
    // @param description 채널 설명
    public Channel() {}

    public Channel(String channelName, String description) {
        this.channelId = UUID.randomUUID(); // 새로운 UUID 생성하여 id 초기화
        this.createdAt = System.currentTimeMillis(); // 현재 시간을 유닉스 타임스탬프로 createdAt 초기화
        this.updatedAt = createdAt; // 초기 updatedAt은 createdAt과 동일하게 설정
        this.channelName = channelName;
        this.description = description;
    }

    //Channel 객체 Getter -> 미션에 Setter가 없으므로 Getter만 생성한다.
    public UUID getChannelId() {
        return channelId;
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

    public String getDescription() {
        return description;
    }

    //Channel 객체의 필드를 수정하는 메서드
    // 수정된 필드에 따라 updatedAt를 현재 시간으로 업데이트한다.
    // @param channelName 새로운 채널 이름 (null이 아니면 업데이트)
    // @param description 새로운 채널 설명 (null이 아니면 업데이트)
    public void updateChannel(String channelName, String description) {
        boolean changed = false; // 변경 여부를 추적하는 플래그

        if(channelName != null && !channelName.equals(this.channelName)) {
            this.channelName = channelName;
            changed = true; //채널 이름 변경안됨
        }

        if(description != null && !description.equals(this.description)) {
            this.description = description;
            changed = true; //채널 설명 변경안됨
        }

        // 필드가 변경되었을 경우에만 updatedAt을 업데이트
        if(changed) {
            this.updatedAt = System.currentTimeMillis();
        }
    }

    //toSting, Override
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Channel{");
        sb.append("channelId=").append(channelId);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", channelName='").append(channelName).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
