package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

//엔티티
@Data
@AllArgsConstructor
@Getter
public class User implements Serializable {

    //직렬화된 객체의 버전을 명시적으로 지정
    @Serial
    private static final long serialVersionUID = 1L;

    //필드
    private final UUID id; // 고유 id (DB용 내부 식별자)
    private final Instant createdAt;
    private Instant updatedAt;

    private String userId; // 사용자 id (로그인용 외부식별자)
    private String email;
    private String password;// 사용자 PW (로그인용 외부식별자)
    private UUID profileId; // User가 가진 프로필 이미지ID
    private UserStatus status; // User 상태


    //기본 생성자
    //매개변수X
    public User() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }


    public User(String userId, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
        this.userId = userId;
        this.password = password;
        this.email = null; // 이메일은 null 처리
    }

    //일반 생성자
    public User(String userId, String password, String email) {
        this.id = UUID.randomUUID(); //생성자 내부 초기화
        this.createdAt = Instant.now(); //생성자 내부 초기화
        this.updatedAt = createdAt; //생성자 내부 초기화, 처음 생성시 수정시간을 생성시간으로 맞춰줌
        this.userId = userId; //파라미터로 받음
        this.password = password; //파라미터로 받음
        this.email = email;
    }


    //복사본 생성자
    public User(User other) {
        this.id = other.id;
        this.createdAt = other.createdAt;
        this.updatedAt = other.updatedAt;
        this.userId = other.userId;
        this.password = other.password;
        this.email = other.email;
    }

    public User(String senderUser, String number, UUID id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    //마지막 수정 시간을 현재시간으로 바꿔주는 메서드
    public void updateTime() {
        this.updatedAt = Instant.now();
    }

    //toString
    @Override
    public String toString() {
        return "User{" + "id=" + id + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", userId='" + userId + '\'' + ", password='" + password + '\'' + ", email='" + email + '\'' + '}';
    }
}
