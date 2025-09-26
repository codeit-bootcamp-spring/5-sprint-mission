package com.sprint.mission.discodeit.entity.base;


import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

/* 각 Entity마다있는 공통 필드의
 * 중복을 제거하기 위해 추상클래스 생성
 * */

/* JPA가 생성자나 setter 없어도 알아서 초기화해줌
 * 엔티티에서 생성자 안받아도 자동 초기화 및 주입
 * */

@MappedSuperclass // 공통 필드 상속 어노테이션
//@EntityListeners(AuditingEntityListener.class) // createdAt 기록해주는 리스너 등록
@Getter
public abstract class BaseEntity {

  @Id
  @GeneratedValue // UUID 자동 생성
  private UUID id;

  @CreatedDate // insert시 생성시간 자동 채움
  @Column(updatable = false) // update 불가
  private Instant createdAt;

  //기본 생성자
  public BaseEntity() {
  }

  public void setId(UUID id) {
    this.id = id;
  }

}
