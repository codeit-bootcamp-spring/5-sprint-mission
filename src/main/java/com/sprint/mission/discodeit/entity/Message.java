package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {

    @Column
    private String content;

    /**
     * 작성 채널: N:1(다대일), Message가 연관관계 주인(FK: channel_id)
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    /**
     * 작성자: N:1(다대일), Message가 연관관계 주인(FK: author_id)
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    /**
     * 첨부파일: N:N 단방향. 재사용 가능성을 고려해 @ManyToMany + 조인테이블
     * - 저장/수정 시 첨부 신규 생성은 cascade=PERSIST만 주는 것이 안전하지만
     *   단순화를 위해 ALL 사용(공유 파일을 삭제하지 않도록 orphanRemoval 미사용)
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id"))
    private List<BinaryContent> attachments = new ArrayList<>();

    protected Message() {}

    public Message(String content, Channel channel, User author) {
        this.content = content;
        this.channel = channel;
        this.author = author;
    }

    public void addAttachment(BinaryContent file) { this.attachments.add(file); }

    public String getContent() { return content; }
    public Channel getChannel() { return channel; }
    public User getAuthor() { return author; }
    public List<BinaryContent> getAttachments() { return attachments; }

    // update 보조 메서드 추가
    public void update(String newContent) {
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
        }
    }

}
