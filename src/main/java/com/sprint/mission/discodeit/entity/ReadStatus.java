package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "read_statuses")
@Getter
@NoArgsConstructor
public class ReadStatus extends BaseUpdatableEntity {

  /* 어떤 유저가 어떤 메시지를 읽었는지 추적
   * 읽은 사용자, 읽은 메세지, 읽은 시간을 가짐
   */

  /* 읽음상태가 사용자 참조 N:1
   * FK 역할
   * */
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  /* 읽음상태가 채널 참조 N:1
   * FK 역할
   * */
  @ManyToOne
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  //마지막으로 읽은 시간
  @Column(name = "last_read_at", nullable = false)
  private Instant lastReadAt;

  public ReadStatus(User user, Channel channel, Instant lastReadAt) {
    this.user = user;
    this.channel = channel;
    this.lastReadAt = lastReadAt;
  }


  //마지막으로 읽은 시간 갱신 메서드
  public void update(Instant newLastReadAt) {
    this.lastReadAt = newLastReadAt;
  }
}
