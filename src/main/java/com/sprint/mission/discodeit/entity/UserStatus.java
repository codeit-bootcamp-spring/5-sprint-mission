package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * 사용자의 마지막 접속 시각을 표현하는 도메인.
 * - 온라인 여부 판단에 사용
 */

@Getter
public class UserStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    // 고유 식별자
    private UUID id;
    // 생성/수정 시각
    private Instant createdAt;
    private Instant updatedAt;

    // 사용자 식별자(단방향 ID 참조키)
    private UUID userId;
    // 마지막 접속 시각
    private Instant lastActiveAt;


    public UserStatus(UUID userId, Instant lastActiveAt){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.userId=userId;
        this.lastActiveAt=lastActiveAt;
    }

    public void update(Instant lastActiveAt) {
        boolean anyValueUpdated = false;

        if ( lastActiveAt != null && this.lastActiveAt.equals(lastActiveAt)){
            this.lastActiveAt = lastActiveAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    // 요구사항 메서드: 현재 시각 기준 온라인 여부
    public boolean isOnline() {
        // 1. 현재 시각에서 5분을 뺀 시각을 구함
        Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));
        // 2. lastActiveAt이 "5분 전 시각" 이후인지 비교
        return lastActiveAt.isAfter(instantFiveMinutesAgo);
    }


}
