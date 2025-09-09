package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name="messages")
public class Message extends BaseUpdatableEntity{
    @Column(name="content")
    private String content;
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="channel_id")
    private Channel channel;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="author_id")
    private User author;
    @OneToMany(fetch = FetchType.LAZY,orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(name="message_attachments",
            joinColumns=@JoinColumn(name="message_id"),
            inverseJoinColumns = @JoinColumn(name="attachment_id"))
    @Builder.Default
    private Set<BinaryContent> attachments = new LinkedHashSet<>();
}
