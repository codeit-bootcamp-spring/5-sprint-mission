package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

//엔티티
public class User implements Serializable {
    //직렬화된 객체의 버전을 명시적으로 지정
    private static final long serialVersionUID = 1L;
    //필드
    private final UUID id; // 고유 id (DB용 내부 식별자)
    private Long createdAt; // 생성 시간
    private Long updatedAt; // 수정 시간
    private String userId; //사용자 id (로그인용 외부식별자)
    private String password;// 사용자 PW (로그인용 외부식별자)


    //기본 생성자
    //매개변수X
    public User() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
    }


    //일반 생성자
    public User(String userId, String password) {
        this.id = UUID.randomUUID(); //생성자 내부 초기화
        this.createdAt = System.currentTimeMillis(); //생성자 내부 초기화
        this.updatedAt = createdAt; //생성자 내부 초기화, 처음 생성시 수정시간을 생성시간으로 맞춰줌
        this.userId = userId; //파라미터로 받음
        this.password = password; //파라미터로 받음
    }


    //복사본 생성자
    public User(User other) {
        this.id = other.id;
        this.createdAt = other.createdAt;
        this.updatedAt = other.updatedAt;
        this.userId = other.userId;
        this.password = other.password;
    }

    //Getter
    public UUID getId() {
        return id;
    }

    public Long getCreateAt() {
        return createdAt;
    }

    public Long getUpdateAt() {
        return updatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    //마지막 수정 시간을 현재시간으로 바꿔주는 메서드
    public void updateTime() {
        this.updatedAt = System.currentTimeMillis();
    }


    //toString
    @Override
    public String toString() {
        return "User{" + "id=" + id + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", userId='" + userId + '\'' + ", password='" + password + '\'' + '}';
    }
}
