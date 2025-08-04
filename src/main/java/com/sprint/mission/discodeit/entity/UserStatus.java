package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델. 사용자의 온라인 상태를 확인하기 위해 활용합니다.
 * <p>{@link #userId} - FK</p>
 * {@link #isOnline()}
 * {@link #updateLastAccessedAt()}
 **/
@Getter
public class UserStatus extends Base implements Serializable {
    private final UUID userId;
    private Instant lastAccessedAt;

    public UserStatus(User user) {
        super(); // 베이스 생성자 호출
        this.userId = user.getId();
        this.lastAccessedAt = Instant.now();
    }

    /**
     * 마지막 접속 시간 갱신
     **/
    public void updateLastAccessedAt() {
        this.lastAccessedAt = Instant.now();
        updateTimestamp(); // updatedAt 필드도 갱신
    }

    /**
     * 온라인 여부 판단
     * @return true: 온라인 / false: 오프라인 또는 정보없음
     **/
    public boolean isOnline() {
        if(lastAccessedAt == null) {
            return false; // 접속 기록이 없으면 무조건 오프라인
        }
        Instant threshold = Instant.now().minus(Duration.ofMinutes(5));
        return lastAccessedAt.isAfter(threshold);
    }
}
