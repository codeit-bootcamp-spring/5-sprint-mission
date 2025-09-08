package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

/**
 * 사용자-채널 단위의 마지막 읽음 시각.
 * - (user_id, channel_id) 유니크 제약
 * - 다대일 관계 양쪽 모두에 매핑(연관관계 주인은 ReadStatus)
 */

@Entity
@Table(name = "read_statuses", uniqueConstraints = {
        @UniqueConstraint(name = "uk_readstatus_user_channel", columnNames = {"user_id", "channel_id"})
})
public class ReadStatus extends BaseUpdatableEntity {

    @Getter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Getter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Getter
    @Column(nullable = false)
    private Instant lastReadAt;

    protected ReadStatus() {}

    public ReadStatus(User user, Channel channel, Instant lastReadAt) {
        this.user = user;
        this.channel = channel;
        this.lastReadAt = lastReadAt;
    }

    public void mark(Instant now) { this.lastReadAt = now; }

}

//    public void update(Instant newLastReadAt) {
//        boolean anyValueUpdated = false;
//
//        if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
//            this.lastReadAt = newLastReadAt;
//            anyValueUpdated = true;
//        }
//
//        if (anyValueUpdated) {
//            this.updatedAt=Instant.now();
//        }
//    }




