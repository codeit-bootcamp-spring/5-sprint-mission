package com.sprint.mission.discodeit.service.dto.user;

import java.time.Instant;
import java.util.UUID;

/** find/findAll 응답 DTO (비밀번호 제외 + 온라인 여부 포함) */
public class UserView {
    public UUID id;
    public String username;
    public String email;
    public UUID profileId;      // null 가능
    public boolean online;      // 마지막 접속 5분 이내 여부
    public Instant lastSeenAt;  // 마지막 접속 시각(참고용)
    public Instant createdAt;
    public Instant updatedAt;

    public UserView(UUID id, String username, String email, UUID profileId,
                    boolean online, Instant lastSeenAt, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profileId = profileId;
        this.online = online;
        this.lastSeenAt = lastSeenAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
