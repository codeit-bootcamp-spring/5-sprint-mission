package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.List;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseUpdatableEntity {

  @Column(name = "content", nullable = false, columnDefinition = "text")
  private String content;

  /** N:1 (B→A 단방향) */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  /** N:1 (B→A 단방향) */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id") // DB에서 ON DELETE SET NULL
  private User author;

  /** 단방향 1:N, 조인 테이블 사용 (message_attachments) */
  @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})  // ← 저장/수정만 전이
  @JoinTable(
      name = "message_attachments",
      joinColumns = @JoinColumn(name = "message_id"),
      inverseJoinColumns = @JoinColumn(name = "attachment_id")
  )
  private List<BinaryContent> attachments = new ArrayList<>();

  public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
    if (content == null || content.isBlank()) throw new IllegalArgumentException("content is required");
    this.content = content;
    this.channel = Objects.requireNonNull(channel, "channel is required");
    this.author  = author; // null 허용
    if (attachments != null) attachments.stream().filter(Objects::nonNull).forEach(this.attachments::add);
  }

  public boolean update(String newContent) {
    if (newContent != null && !newContent.equals(this.content)) { this.content = newContent; return true; }
    return false;
  }

  public List<BinaryContent> getAttachments() { return Collections.unmodifiableList(attachments); }
  public void addAttachment(BinaryContent f) { if (f != null) attachments.add(f); }
  public void addAttachments(List<BinaryContent> fs){ if (fs != null) fs.stream().filter(Objects::nonNull).forEach(attachments::add); }
  public void clearAttachments() { attachments.clear(); }
}