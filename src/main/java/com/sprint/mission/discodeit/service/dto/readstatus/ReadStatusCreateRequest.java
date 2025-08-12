package com.sprint.mission.discodeit.service.dto.readstatus;

public class ReadStatusCreateRequest {
    // 어떤 사용자가 어떤 채널에서 읽음 포인터를 어디까지 옮겼는지 저장
    private Long userId;
    private Long channelId;
    // 예시 필드: 마지막으로 읽은 메시지 ID(또는 오프셋/시각 등으로 대체 가능)
    private Long lastReadMessageId;

    public ReadStatusCreateRequest() {}

    public ReadStatusCreateRequest(Long userId, Long channelId, Long lastReadMessageId) {
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadMessageId = lastReadMessageId;
    }

    public Long getUserId() { return userId; }
    public Long getChannelId() { return channelId; }
    public Long getLastReadMessageId() { return lastReadMessageId; }
}
