package com.sprint.mission.discodeit.message.domain;

import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.common.domain.BaseUpdatableEntity;
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
@Table(name = "messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseUpdatableEntity {

    public static final int CONTENT_MAX_LENGTH = 2000;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User author;

    public Message(
        String content,
        Channel channel,
        User author
    ) {
        if (content != null) {
            validateContent(content);
        }
        if (channel == null) {
            throw new IllegalArgumentException("Channel cannot be null.");
        }
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null.");
        }

        this.content = content;
        this.channel = channel;
        this.author = author;
    }

    public Message update(String newContent) {
        if (newContent != null) {
            validateContent(newContent);
            this.content = newContent;
        }
        return this;
    }

    private void validateContent(String content) {
        if (content.length() > CONTENT_MAX_LENGTH) {
            throw new IllegalArgumentException("Content length must not exceed " + CONTENT_MAX_LENGTH);
        }
    }
}
