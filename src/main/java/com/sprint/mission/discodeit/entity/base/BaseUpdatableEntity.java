package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import org.springframework.data.annotation.LastModifiedDate;


/* BaseEntity를 상속받고,
 * updatedAt만 추가된 클래스
 * */

@MappedSuperclass
public class BaseUpdatableEntity extends BaseEntity {

  @LastModifiedDate // 수정시간 update시 자동으로 채워짐
  private Instant updatedAt;

  //부모 기본 생성자 호출
  public BaseUpdatableEntity() {
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

}
