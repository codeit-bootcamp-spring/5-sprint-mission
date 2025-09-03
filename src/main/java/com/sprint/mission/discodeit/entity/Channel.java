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
import lombok.Setter;

@Entity
@Table(name = "channels")
@Getter
@Setter
@NoArgsConstructor
public class Channel extends BaseUpdatableEntity {

  @Column(name = "name", length = 100)
  private String name; // 채널 이름

  @Column(name = "description", length = 500)
  private String description; // 채널 설명

  @Enumerated(EnumType.STRING)
  @Column(name = "type", length = 10, nullable = false)
  private ChannelType channelType; // 음성채널 or 일반채널

  @Column(name = "last_message_at")
  private Instant lastMessageAt;


  public Channel(String name, ChannelType privateChannel) {

  }

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
