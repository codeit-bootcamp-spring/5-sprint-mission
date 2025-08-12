package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

// 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델입니다.
// 사용자의 온라인 상태를 확인하기 위해 활용합니다.
// 마지막 접속 시간을 기준으로 현재 로그인한 유저로 판단할 수 있는 메소드를 정의
// 마지막 접속 시간이 현재 시간으로부터 5분 이내이면 현재 접속 중인 유저로 간주

@Getter
@ToString
public class UserStatus {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID userId;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void update(){
        this.updatedAt = Instant.now();
    }

    public boolean isOnline(){
        return updatedAt.isAfter(Instant.now().minusSeconds(300));
    }
}
