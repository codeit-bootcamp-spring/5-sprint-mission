package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;
    private String content;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id"))
    private List<BinaryContent> attachments;

    public Message(Channel channel, User author, String content, List<BinaryContent> attachments) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel ID is required");
        }
        if (author == null) {
            throw new IllegalArgumentException("Author ID is required");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content is required");
        }

        this.channel = channel;
        this.author = author;
        this.content = content;
        this.attachments = attachments;
    }

    public void editContent(String content) {
        if (content != null && !content.equals(this.content)) {
            this.content = content;
        }
    }
}
