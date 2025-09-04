package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "channels")
@Getter
@NoArgsConstructor
public class Channel extends BaseUpdatableEntity {

  /* 채널 이름, 설명, 타입(공개/비공개)와,
   * 마지막 메시지 시간, 채널 참여자(유저)를 가짐
   */

  //채널 이름
  @Column(name = "name", length = 100)
  private String name;

  // 채널 설명
  @Column(name = "description", length = 500)
  private String description;

  // 음성채널 or 일반채널
  @Enumerated(EnumType.STRING)
  @Column(name = "type", length = 10, nullable = false)
  private ChannelType channelType;

  // 마지막 읽은 시간
  @Column(name = "last_message_at")
  private Instant lastMessageAt;

  /* 채널 : 참여자(유저) N:N
   * 중간 테이블 만들어서 양쪽 PK를 FK로 저장
   * 연관관계의 주인
   * */
  @ManyToMany
  @JoinTable(
      name = "channel_participants",
      joinColumns = @JoinColumn(name = "channel_id"), // 채널 PK값이 FK로 들어감
      inverseJoinColumns = @JoinColumn(name = "user_id") // 반대쪽 PK값이 FK로 들어감
  )
  private List<User> participants;


  //일반 생성자
  public Channel(String name, String description, ChannelType channelType) {
    this.name = name;
    this.description = description;
    this.channelType = channelType;
  }

  //복사본 생성자
  public Channel(Channel other) {
    this.name = other.name;
    this.channelType = other.channelType;
  }

  //메서드
  public void updateTime() {
    this.lastMessageAt = Instant.now();
  }

  //toString
  @Override
  public String toString() {
    return "Channel{" +
        "name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", channelType=" + channelType +
        ", lastMessageAt=" + lastMessageAt +
        ", participants=" + participants +
        "} " + super.toString();
  }
}
