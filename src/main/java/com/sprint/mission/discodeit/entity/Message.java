package com.sprint.mission.discodeit.entity;


import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User author;

    @Override
    public String toString() {
        return "Message[id=%s, channelId=%s, authorId=%s, content=%s, createdAt=%s]"
            .formatted(
                getId(),
                channel != null ? channel.getId() : null,
                author != null ? author.getId() : null,
                content != null && content.length() > 30
                    ? content.substring(0, 30) + "..."
                    : content,
                getCreatedAt()
            );
    }
}
