package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델.
 * 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용합니다.
 * <p>{@link #userId} FK</p>
 * <p>{@link #channelId} FK</p>
 * <p>{@link #lastReadAt}</p>
 * <p>{@link #updateLastReadAt(Instant)}</p>
 * <p>{@link #isRead(Instant)}</p>
 **/
@Getter
public class ReadStatus extends Base {
    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(User user, Channel channel) {
        super(); // id, createdAt 초기화
        this.userId = user.getId();
        this.channelId = channel.getId();
        this.lastReadAt = null; // 아직 읽은 메시지 없음
    }

    /**
     * 마지막으로 읽은 시간 갱신
     * @param readAt 사용자가 마지막으로 읽은 메시지의 생성 시각
     **/
    public void updateLastReadAt(Instant readAt) {
        this.lastReadAt = readAt;
        updateTimestamp();
    }

    /**
     * 주어진 메시지의 상태 확인
     * @param messageCreatedAt 메시지의 생성 시각
     * @return true면 이미 읽은 메시지, false면 안 읽은 메시지
     */
    public boolean isRead(Instant messageCreatedAt) {
        return lastReadAt != null && !messageCreatedAt.isAfter(lastReadAt);
    }
}
