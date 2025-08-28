package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(
    name = "messages",
    indexes = @Index(
        name = "idx_messages_channel_created",
        columnList = "channel_id, created_at DESC"
    )
)
public class Message extends BaseUpdatableEntity {

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "channel_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "messages_channel_id_fkey")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "author_id",
        foreignKey = @ForeignKey(name = "messages_author_id_fkey")
    )
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User author;

    @ManyToMany
    @JoinTable(
        name = "message_attachments",
        joinColumns = @JoinColumn(name = "message_id"),
        inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    @OrderColumn(name = "order_index")
    private List<BinaryContent> attachments = new ArrayList<>();

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
