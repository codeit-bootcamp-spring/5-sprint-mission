package com.sprint.mission.discodeit.domain.entity;

import com.sprint.mission.discodeit.util.Validators;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "messages")
@Check(constraints = "LENGTH(content) <= 4000")
public class Message extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(length = 4000)
    private String content;

    @ElementCollection
    @CollectionTable(name = "message_files", joinColumns = @JoinColumn(name = "message_id"))
    @Column(name = "file_url")
    private final Set<String> files = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to")
    private Message replyTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    public Message(ChatRoom chatRoom, User sender, String content, Set<String> files, Message replyTo) {
        this.chatRoom = Objects.requireNonNull(chatRoom, "Chat room must not be null.");
        this.sender = Objects.requireNonNull(sender, "Sender must not be null.");
        setContent(content);
        setFiles(files);
        this.replyTo = replyTo;
    }

    public void setContent(String content) {
        this.content = Validators.validateMessageContent(content);
    }


    public void setFiles(Set<String> files) {
        this.files.clear();
        if (files == null || files.isEmpty()) return;
        for (String file : files) Validators.validateUri(file);
        this.files.addAll(files);
    }

    @Override
    public String toString() {
        return String.format("Message[chatRoom=%s, sender=%s, content='%s', files=%d, ]",
                chatRoom, sender, content, files.size());
    }
}
