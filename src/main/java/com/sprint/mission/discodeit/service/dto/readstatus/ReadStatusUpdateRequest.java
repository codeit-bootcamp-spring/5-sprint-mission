package com.sprint.mission.discodeit.service.dto.readstatus;

public class ReadStatusUpdateRequest {
    // 업데이트할 값만 담는 DTO. 확장성을 위해 nullable 허용
    private Long lastReadMessageId;

    public ReadStatusUpdateRequest() {}

    public ReadStatusUpdateRequest(Long lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;
    }

    public Long getLastReadMessageId() { return lastReadMessageId; }
}