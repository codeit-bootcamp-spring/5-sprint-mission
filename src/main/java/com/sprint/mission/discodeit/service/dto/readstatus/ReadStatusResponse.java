package com.sprint.mission.discodeit.service.dto.readstatus;

// 응답 DTO (서비스 바깥으로 노출)
public class ReadStatusResponse {
    private Long id;
    private Long userId;
    private Long channelId;
    private Long lastReadMessageId;

    public ReadStatusResponse(Long id, Long userId, Long channelId, Long lastReadMessageId) {
        this.id = id;
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadMessageId = lastReadMessageId;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getChannelId() { return channelId; }
    public Long getLastReadMessageId() { return lastReadMessageId; }
}
