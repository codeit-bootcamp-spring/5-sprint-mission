package com.sprint.mission.discodeit.entity;

import java.util.UUID; // UUID를 사용하기 위한 import


//채널 도메인 모델 클래스
// 채널 고유 ID, 생성 시간, 수정 시간, 이름, 채널 설명을 관리한다.
public class Message {
    private UUID messageId; //객체를 식별하기 위한 고유 id (UUID type)
    private Long createdAt; // 객체 생성 시간 저장 (Unix time stamp, Long type)
    private Long updatedAt; // 객체 수정 시간 (Unix time stamp, Long type)
    private String content; //메세지 내용
    private UUID userId; // 메세지를 작성한 사용자의 ID (User 도메인 모델의 id와 연동)
    private UUID channelId; // 메세지가 전송된 채널의 ID (Channel 도메인 모델의 id와 연동)

    // Message 객체의 생성자입니다.
    // id와 createdAt은 생성 시점에 초기화되며, 다른 필드들은 파라미터를 통해 초기화됩니다.
    // @param content 메세지 내용
    // @param userId  메세지를 작성한 사용자의 ID
    // @param channelId 메세지가 전송된 채널의 ID

    public Message() {}

    public Message(String content, UUID userId, UUID channelId) {
        this.messageId = UUID.randomUUID(); // 새로운 UUID 생성하여 id 초기화
        this.createdAt = System.currentTimeMillis(); // 현재 시간을 유닉스 타임스탬프로 createdAt 초기화
        this.updatedAt = createdAt; // 초기 updatedAt은 createdAt과 동일하게 설정
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
    }
    //Message 객체 Getter -> 미션에 Setter가 없으므로 Getter만 생성한다.

    public UUID getMessageId() {
        return messageId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getContent() {
        return content;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
    }


    //Channel 객체의 필드를 수정하는 메서드
    // 수정된 필드에 따라 updatedAt를 현재 시간으로 업데이트한다.
    // @param content 새로운 메세지 내용 (null이 아니면 업데이트)
    //
    public void updateMessage(String content) {
        if(content != null && !content.equals(this.content)) {
            this.content = content;
            this.updatedAt = System.currentTimeMillis(); // 내용이 변경되었으므로 updatedAt 업데이트
        }
    }

    //toSting, Override
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append("messageId=").append(messageId);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", content='").append(content).append('\'');
        sb.append(", userId=").append(userId);
        sb.append(", channelId=").append(channelId);
        sb.append('}');
        return sb.toString();
    }
}

