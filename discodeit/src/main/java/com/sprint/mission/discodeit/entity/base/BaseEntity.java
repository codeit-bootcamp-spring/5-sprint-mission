package com.sprint.mission.discodeit.entity.base;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter @ToString
@MappedSuperclass // 공통 Entity를 알리는 어노테이션
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 보안적인 용도
@SuperBuilder // lombok용 자식이 부모 빌더를 접근할수 있도록 돕는 어노테이션
public abstract class BaseEntity {

     // 테이블의 PK임을 알리는 어노테이션
//    @GeneratedValue(strategy = GenerationType.IDENTITY)  // PK가 자동으로 증가되는데, DB에게 위임한다는 어노테이션
    @Id
    @Column(updatable = false, nullable = false) // 이름이 불일치하면 name으로 컬럼명을 적는다.
    protected UUID id=UUID.randomUUID();

    @CreatedDate // 자동으로 생성되는 날짜임을 알리는 어노테이션
    @Column(nullable = true)
    protected Instant createdAt;



}
