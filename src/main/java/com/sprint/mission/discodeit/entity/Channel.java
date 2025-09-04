package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "channels")
@Getter
@NoArgsConstructor
public class Channel extends BaseUpdatableEntity {

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


  //일반 생성자 (1)
  public Channel(String name, ChannelType channelType) {
    this.name = name;
    this.channelType = channelType;
  }


  //일반 생성자 (2)
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
        "} " + super.toString();
  }
}
