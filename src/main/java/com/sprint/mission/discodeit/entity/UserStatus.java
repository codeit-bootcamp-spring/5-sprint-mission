package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;

@Getter // ✅ 조회용 Getter만 공개
@Entity
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

    @Setter
    @OneToOne                                  // 1:1 단방향(여기서 FK 가짐)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;                         // 소유 사용자

    @Column(nullable = false)
    private Instant lastActiveAt;              // 마지막 활동 시각

    protected UserStatus() { }                 // JPA 기본 생성자

    public UserStatus(Instant lastActiveAt) {
        this.lastActiveAt = lastActiveAt;      // 마지막 활동 시각 설정
    }


    /** 최근 5분 내 활동 여부 */
    public boolean isOnline() {
        Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5)); // 5분 전
        return lastActiveAt.isAfter(instantFiveMinutesAgo);                          // 이후면 온라인
    }

    /** 활동 시간 갱신(옵션) */
    public void touch(Instant now) {
        this.lastActiveAt = now;               // 활동 시각 업데이트
    }
}
