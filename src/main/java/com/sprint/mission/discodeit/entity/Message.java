package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity // JPA 엔티티 객체로 지정
@Table(name = "messages")
@Getter @Setter
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseUpdatableEntity{

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

   @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
   @JoinTable(name = "message_attachments",
       joinColumns = @JoinColumn(name = "message_id"),
       inverseJoinColumns = @JoinColumn(name = "attachment_id"))
    private List<BinaryContent> attachments = new ArrayList<>();

    public void update(String newContent) {
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
        }
    }
}
