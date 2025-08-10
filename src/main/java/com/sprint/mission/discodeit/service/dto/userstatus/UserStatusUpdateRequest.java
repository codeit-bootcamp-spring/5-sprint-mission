package com.sprint.mission.discodeit.service.dto.userstatus;

import java.time.Instant;

/** 부분 수정 DTO (null/미지정은 미반영) */
public class UserStatusUpdateRequest {

    private Boolean online;       // null 아닐 때만 업데이트
    private Instant lastSeenAt;   // null 아닐 때만 업데이트

    public UserStatusUpdateRequest() {}

    public UserStatusUpdateRequest(Boolean online, Instant lastSeenAt) {
        this.online = online;
        this.lastSeenAt = lastSeenAt;
    }

    public Boolean getOnline() { return online; }
    public Instant getLastSeenAt() { return lastSeenAt; }
}

