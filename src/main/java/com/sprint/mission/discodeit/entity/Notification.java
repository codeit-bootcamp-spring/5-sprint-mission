package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User receiver;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private boolean checked;

    public Notification(User receiver, String title, String content) {
        if (receiver == null) {
            throw new IllegalArgumentException("receiver must not be null");
        }
        if (title == null) {
            throw new IllegalArgumentException("title must not be null");
        }
        if (content == null) {
            throw new IllegalArgumentException("content must not be null");
        }

        this.receiver = receiver;
        this.title = title;
        this.content = content;
    }

    public void check() {
        this.checked = true;
    }

    @Override
    public String toString() {
        return "Notification[receiverId=%s, title=%s, checked=%s]"
            .formatted(
                receiver != null ? receiver.getId() : null,
                title,
                checked
            );
    }
}
