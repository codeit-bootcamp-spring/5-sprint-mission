package com.sprint.mission.discodeit.notification.domain;

import com.sprint.mission.discodeit.common.domain.BaseEntity;
import com.sprint.mission.discodeit.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    private static final int TITLE_MAX_LENGTH = 100;
    private static final int CONTENT_MAX_LENGTH = 500;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User receiver;

    @Column(nullable = false, length = TITLE_MAX_LENGTH)
    private String title;

    @Column(nullable = false, length = CONTENT_MAX_LENGTH)
    private String content;

    private boolean checked;

    public Notification(User receiver, String title, String content) {
        if (receiver == null) {
            throw new IllegalArgumentException("receiver must not be null");
        }
        if (title == null) {
            throw new IllegalArgumentException("title must not be null");
        } else if (title.length() > TITLE_MAX_LENGTH) {
            throw new IllegalArgumentException("title length must not exceed " + TITLE_MAX_LENGTH);
        }
        if (content == null) {
            throw new IllegalArgumentException("content must not be null");
        } else if (content.length() > CONTENT_MAX_LENGTH) {
            throw new IllegalArgumentException("content length must not exceed " + CONTENT_MAX_LENGTH);
        }

        this.receiver = receiver;
        this.title = title;
        this.content = content;
    }

    public Notification check() {
        this.checked = true;
        return this;
    }
}
